package com.example.training.data.dto

data class CategoryDto(
    val name: String,
    val color: Long,
    val iconName: String
) {
    constructor() : this("", 0L, "")
}
