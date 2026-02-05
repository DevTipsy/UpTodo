package com.example.training.model

data class Task(
    val id: String,
    val title: String,
    val date: Long,  // Timestamp en millisecondes
    val category: String,
    val userId: String,
    val isCompleted: Boolean
)
