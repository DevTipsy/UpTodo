package com.example.training.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.training.model.Categories
import com.example.training.model.Category
import com.google.firebase.firestore.FirebaseFirestore

class CategoryViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    var categories by mutableStateOf<List<Category>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    init {
        loadCategories()
    }

    fun loadCategories() {
        isLoading = true
        firestore.collection("categories")
            .get()
            .addOnSuccessListener { snapshot ->
                categories = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Category::class.java)?.copy(id = doc.id)
                }
                isLoading = false

                // Si aucune catégorie n'existe, initialiser avec les catégories par défaut
                if (categories.isEmpty()) {
                    initializeDefaultCategories()
                }
            }
            .addOnFailureListener {
                isLoading = false
                // En cas d'erreur, utiliser les catégories par défaut localement
                categories = Categories.default
            }
    }

    private fun initializeDefaultCategories() {
        isLoading = true
        val batch = firestore.batch()

        Categories.default.forEach { category ->
            val docRef = firestore.collection("categories").document()
            batch.set(docRef, category.copy(id = docRef.id))
        }

        batch.commit()
            .addOnSuccessListener {
                loadCategories() // Recharger après initialisation
            }
            .addOnFailureListener {
                isLoading = false
                // En cas d'erreur, utiliser les catégories par défaut localement
                categories = Categories.default
            }
    }
}
