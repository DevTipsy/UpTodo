package com.example.training.repository

import com.example.training.data.dto.CategoryDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class CategoryRepository {
    private val firestore = FirebaseFirestore.getInstance()

    /**
     * Observer les catégories en temps réel
     * Retourne Flow<List<CategoryDto>>
     **/
    fun observeCategories(): Flow<List<CategoryDto>> = callbackFlow {
        val listener = firestore.collection("categories")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val categoryDtos = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        val name = doc.getString("name")
                        val color = doc.getLong("color")
                        val iconName = doc.getString("iconName")

                        // Vérifier que tous les champs requis sont présents
                        if (name != null && color != null && iconName != null) {
                            CategoryDto(
                                name = name,
                                color = color,
                                iconName = iconName
                            )
                        } else {
                            null  // Ignorer les documents avec des champs manquants
                        }
                    } catch (e: Exception) {
                        null  // Ignorer les documents malformés
                    }
                } ?: emptyList()

                trySend(categoryDtos).isSuccess
            }

        awaitClose { listener.remove() }
    }
}
