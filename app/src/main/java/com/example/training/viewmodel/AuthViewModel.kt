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

    var currentUser by mutableStateOf<User?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        // Charger l'utilisateur au démarrage si connecté
        auth.currentUser?.let { firebaseUser ->
            loadUserFromFirestore(firebaseUser.uid)
        }
    }

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
                                currentUser = user
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
                    val firebaseUser = auth.currentUser
                    if (firebaseUser != null) {
                        // Charger le User depuis Firestore
                        loadUserFromFirestore(firebaseUser.uid) { success ->
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

    private fun loadUserFromFirestore(userId: String, onComplete: ((Boolean) -> Unit)? = null) {
        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    currentUser = document.toObject(User::class.java)
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
        currentUser = null
    }

    fun clearError() {
        errorMessage = null
    }
}
