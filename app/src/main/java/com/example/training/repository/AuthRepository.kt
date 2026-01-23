package com.example.training.repository

import com.example.training.data.dto.UserDto
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
}
