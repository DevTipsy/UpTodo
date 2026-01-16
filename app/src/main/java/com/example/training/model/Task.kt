package com.example.training.model

data class Task(
    val id: String = "",
    val title: String = "",
    val date: Long = 0L,  // Timestamp en millisecondes
    val category: String = "",
    val userId: String = "",
    val isCompleted: Boolean = false
)
