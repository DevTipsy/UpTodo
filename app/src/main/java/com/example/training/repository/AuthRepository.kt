package com.example.training.repository

import com.example.training.R
import com.example.training.data.dto.UserDto
import com.example.training.util.ErrorMapper
import com.example.training.util.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    /**
     * Connexion avec email/password
     * Retourne Result<UserDto> - Le ViewModel utilisera le mapper
     */
    suspend fun signIn(email: String, password: String): Result<UserDto> {
        return try {
            // Authentifier avec Firebase Auth
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
                ?: return Result.Error(Exception("Utilisateur null après connexion"))

            // Charger le UserDto depuis Firestore
            val userDoc = firestore.collection("users")
                .document(firebaseUser.uid)
                .get()
                .await()

            val userDto = userDoc.toObject(UserDto::class.java)
                ?: return Result.Error(Exception("Profil utilisateur introuvable"))

            Result.Success(userDto)
        } catch (e: FirebaseAuthException) {
            val message = when (e.errorCode) {
                "ERROR_INVALID_EMAIL" -> "Email invalide"
                "ERROR_WRONG_PASSWORD" -> "Mot de passe incorrect"
                "ERROR_USER_NOT_FOUND" -> "Aucun compte avec cet email"
                "ERROR_USER_DISABLED" -> "Compte désactivé"
                else -> "Erreur de connexion : ${e.message}"
            }
            Result.Error(e, message)
        } catch (e: Exception) {
            Result.Error(e, "Erreur de connexion")
        }
    }

    /**
     * Inscription avec email/password + profil
     * Retourne Result<UserDto>
     */
    suspend fun signUp(
        email: String,
        password: String,
        prenom: String,
        nom: String
    ): Result<UserDto> {
        return try {
            // Créer le compte Firebase Auth
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
                ?: return Result.Error(Exception("Échec de création du compte"))

            // Créer le UserDto
            val userDto = UserDto(
                id = firebaseUser.uid,
                prenom = prenom,
                nom = nom,
                email = email,
                photoUrl = null
            )

            // Sauvegarder dans Firestore
            try {
                firestore.collection("users")
                    .document(firebaseUser.uid)
                    .set(userDto)
                    .await()
            } catch (firestoreError: Exception) {
                // Rollback : supprimer le compte Auth si Firestore échoue
                firebaseUser.delete().await()
                return Result.Error(
                    firestoreError,
                    "Erreur lors de la création du profil"
                )
            }

            Result.Success(userDto)
        } catch (e: FirebaseAuthException) {
            val message = when (e.errorCode) {
                "ERROR_WEAK_PASSWORD" -> "Mot de passe trop faible (min 6 caractères)"
                "ERROR_EMAIL_ALREADY_IN_USE" -> "Cet email est déjà utilisé"
                "ERROR_INVALID_EMAIL" -> "Email invalide"
                else -> "Erreur d'inscription : ${e.message}"
            }
            Result.Error(e, message)
        } catch (e: Exception) {
            Result.Error(e, "Erreur d'inscription")
        }
    }

    /**
     * Récupérer l'utilisateur actuel depuis Firestore
     * Retourne UserDto? (null si pas connecté ou pas trouvé)
     */
    suspend fun getCurrentUser(): UserDto? {
        return try {
            val firebaseUser = auth.currentUser ?: return null

            val userDoc = firestore.collection("users")
                .document(firebaseUser.uid)
                .get()
                .await()

            userDoc.toObject(UserDto::class.java)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Déconnexion
     */
    fun signOut() {
        auth.signOut()
    }

    /**
     * Mettre à jour le nom de l'utilisateur
     */
    suspend fun updateUserName(prenom: String, nom: String): Result<Unit> {
        return try {
            val firebaseUser = auth.currentUser
                ?: return Result.Error(
                    Exception("Utilisateur non connecté"),
                    R.string.error_user_not_connected
                )

            firestore.collection("users")
                .document(firebaseUser.uid)
                .update(
                    mapOf(
                        "prenom" to prenom,
                        "nom" to nom
                    )
                )
                .await()

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, R.string.error_update_name)
        }
    }

    /**
     * Mettre à jour l'email de l'utilisateur
     * Met à jour l'email dans Firestore ET Firebase Auth
     */
    suspend fun updateUserEmail(newEmail: String): Result<Unit> {
        return try {
            val firebaseUser = auth.currentUser
                ?: return Result.Error(
                    Exception("Utilisateur non connecté"),
                    R.string.error_user_not_connected
                )

            val oldEmail = firebaseUser.email

            // 1. Mettre à jour l'email dans Firestore d'abord
            firestore.collection("users")
                .document(firebaseUser.uid)
                .update("email", newEmail)
                .await()

            // 2. Mettre à jour l'email dans Firebase Auth
            try {
                firebaseUser.updateEmail(newEmail).await()
            } catch (authError: Exception) {
                // Si Firebase Auth échoue, rollback Firestore
                if (oldEmail != null) {
                    firestore.collection("users")
                        .document(firebaseUser.uid)
                        .update("email", oldEmail)
                        .await()
                }
                throw authError
            }

            Result.Success(Unit)
        } catch (e: FirebaseAuthException) {
            Result.Error(e, ErrorMapper.mapAuthError(e))
        } catch (e: Exception) {
            Result.Error(e, R.string.error_update_email)
        }
    }

    /**
     * Mettre à jour le mot de passe de l'utilisateur
     */
    suspend fun updateUserPassword(newPassword: String): Result<Unit> {
        return try {
            val firebaseUser = auth.currentUser
                ?: return Result.Error(
                    Exception("Utilisateur non connecté"),
                    R.string.error_user_not_connected
                )

            // Mettre à jour le mot de passe
            firebaseUser.updatePassword(newPassword).await()

            Result.Success(Unit)
        } catch (e: FirebaseAuthException) {
            Result.Error(e, ErrorMapper.mapAuthError(e))
        } catch (e: Exception) {
            Result.Error(e, R.string.error_update_password)
        }
    }
}
