package com.example.training.data.mapper

import com.example.training.data.dto.CategoryDto
import com.example.training.model.Category

fun CategoryDto.toDomain(): Category {
    return Category(
        id = "",  // CategoryDto n'a pas d'id, on utilise une string vide
        name = this.name,
        color = this.color,
        iconName = this.iconName
    )
}

fun Category.toDto(): CategoryDto {
    return CategoryDto(
        name = this.name,
        color = this.color,
        iconName = this.iconName
    )
}
