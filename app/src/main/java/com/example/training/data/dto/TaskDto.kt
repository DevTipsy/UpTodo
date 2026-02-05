package com.example.training.data.dto

data class TaskDto(
    val id: String,
    val title: String,
    val date: Long,
    val category: String,
    val userId: String,
    val isCompleted: Boolean
) {
    constructor() : this("", "", 0L, "", "", false)
}
