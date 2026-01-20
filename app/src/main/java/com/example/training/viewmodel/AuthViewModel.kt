package com.example.training.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.training.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    // On lit directement auth.currentUser
    var userProfile by mutableStateOf<User?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        // Charger le profil utilisateur au démarrage si connecté
        auth.currentUser?.let { firebaseUser ->
            loadUserProfile(firebaseUser.uid)
        }
    }

    // Helper pour vérifier si l'utilisateur est connecté
    val isUserLoggedIn: Boolean
        get() = auth.currentUser != null

    // Helper pour récupérer l'ID de l'utilisateur connecté
    fun getCurrentUserId(): String? = auth.currentUser?.uid

    fun signUp(email: String, password: String, prenom: String, nom: String, onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank() || prenom.isBlank() || nom.isBlank()) {
            errorMessage = "Tous les champs sont requis"
            return
        }

        isLoading = true
        errorMessage = null

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // On utilise directement auth.currentUser (source de vérité)
                    val firebaseUser = auth.currentUser
                    if (firebaseUser != null) {
                        // Créer le document User dans Firestore
                        val user = User(
                            id = firebaseUser.uid,
                            prenom = prenom,
                            nom = nom,
                            email = email,
                            photoUrl = null
                        )

                        firestore.collection("users")
                            .document(firebaseUser.uid)
                            .set(user)
                            .addOnSuccessListener {
                                userProfile = user
                                isLoading = false
                                onSuccess()
                            }
                            .addOnFailureListener { e ->
                                isLoading = false
                                errorMessage = "Erreur lors de la création du profil: ${e.message}"
                            }
                    }
                } else {
                    isLoading = false
                    errorMessage = task.exception?.message ?: "Erreur lors de l'inscription"
                }
            }
    }

    fun signIn(email: String, password: String, onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Email et mot de passe requis"
            return
        }

        isLoading = true
        errorMessage = null

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // On utilise directement auth.currentUser (source de vérité)
                    val firebaseUser = auth.currentUser
                    if (firebaseUser != null) {
                        // Charger le profil utilisateur depuis Firestore
                        loadUserProfile(firebaseUser.uid) { success ->
                            isLoading = false
                            if (success) {
                                onSuccess()
                            } else {
                                errorMessage = "Erreur lors du chargement du profil"
                            }
                        }
                    }
                } else {
                    isLoading = false
                    errorMessage = task.exception?.message ?: "Erreur lors de la connexion"
                }
            }
    }

    private fun loadUserProfile(userId: String, onComplete: ((Boolean) -> Unit)? = null) {
        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    userProfile = document.toObject(User::class.java)
                    onComplete?.invoke(true)
                } else {
                    onComplete?.invoke(false)
                }
            }
            .addOnFailureListener {
                onComplete?.invoke(false)
            }
    }

    fun signOut() {
        auth.signOut()
        userProfile = null
    }

    fun clearError() {
        errorMessage = null
    }
}
