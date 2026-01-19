package com.example.training.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.training.model.Categories
import com.example.training.model.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TaskViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var tasks by mutableStateOf<List<Task>>(emptyList())
        private set

    fun addTask(title: String, date: Long, category: String, onSuccess: () -> Unit) {
        val userId = auth.currentUser?.uid ?: return

        isLoading = true
        errorMessage = null

        val task = hashMapOf(
            "title" to title,
            "date" to date,
            "category" to category,
            "userId" to userId,
            "isCompleted" to false
        )

        firestore.collection("tasks")
            .add(task)
            .addOnSuccessListener {
                isLoading = false
                onSuccess()
            }
            .addOnFailureListener { exception ->
                isLoading = false
                errorMessage = exception.message
            }
    }

    fun loadTasks() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("tasks")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    errorMessage = error.message
                    return@addSnapshotListener
                }

                tasks = snapshot?.documents?.mapNotNull { doc ->
                    Task(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        date = doc.getLong("date") ?: 0L,
                        category = doc.getString("category") ?: "",
                        userId = doc.getString("userId") ?: "",
                        isCompleted = doc.getBoolean("isCompleted") ?: false
                    )
                } ?: emptyList()
            }
    }
}
