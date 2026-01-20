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
            }
            .addOnFailureListener {
                isLoading = false
                // En cas d'erreur, liste vide
                categories = emptyList()
            }
    }

}
