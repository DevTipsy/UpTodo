package com.example.training.repository

import com.example.training.data.dto.TaskDto
import com.example.training.util.Result
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class TaskRepository {
    private val firestore = FirebaseFirestore.getInstance()

    /**
     * Observer les tâches en temps réel
     * Retourne Flow<List<TaskDto>> - Le ViewModel utilisera le mapper
     */
    fun observeTasks(userId: String): Flow<List<TaskDto>> = callbackFlow {
        val listener = firestore.collection("tasks")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val taskDtos = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        val title = doc.getString("title")
                        val date = doc.getLong("date")
                        val category = doc.getString("category")
                        val userId = doc.getString("userId")
                        val isCompleted = doc.getBoolean("isCompleted")

                        // Vérifier que tous les champs requis sont présents
                        if (title != null && date != null && category != null && userId != null && isCompleted != null) {
                            TaskDto(
                                id = doc.id,
                                title = title,
                                date = date,
                                category = category,
                                userId = userId,
                                isCompleted = isCompleted
                            )
                        } else {
                            null  // Ignorer les documents avec des champs manquants
                        }
                    } catch (e: Exception) {
                        null  // Ignorer les documents malformés
                    }
                } ?: emptyList()

                trySend(taskDtos)
            }

        awaitClose {
            listener.remove()
        }
    }

    /**
     * Ajouter une tâche
     * Prend un TaskDto en paramètre
     */
    suspend fun addTask(taskDto: TaskDto): Result<Unit> {
        return try {
            val taskData = hashMapOf(
                "title" to taskDto.title,
                "date" to taskDto.date,
                "category" to taskDto.category,
                "userId" to taskDto.userId,
                "isCompleted" to taskDto.isCompleted
            )

            firestore.collection("tasks").add(taskData).await()

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, "Erreur lors de l'ajout de la tâche")
        }
    }

    /**
     * Mettre à jour une tâche (optionnel - pour plus tard)
     */
    suspend fun updateTask(taskId: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            firestore.collection("tasks")
                .document(taskId)
                .update(updates)
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, "Erreur lors de la mise à jour")
        }
    }

    /**
     * Supprimer une tâche (optionnel - pour plus tard)
     */
    suspend fun deleteTask(taskId: String): Result<Unit> {
        return try {
            firestore.collection("tasks")
                .document(taskId)
                .delete()
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, "Erreur lors de la suppression")
        }
    }
}
