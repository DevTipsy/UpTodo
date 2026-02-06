package com.example.training.data.mapper

import com.example.training.data.dto.TaskDto
import com.example.training.model.Task

fun TaskDto.toDomain(): Task {
    return Task(
        id = this.id,
        title = this.title,
        date = this.date,
        category = this.category,
        userId = this.userId,
        isCompleted = this.isCompleted
    )
}

fun Task.toDto(): TaskDto {
    return TaskDto(
        id = this.id,
        title = this.title,
        date = this.date,
        category = this.category,
        userId = this.userId,
        isCompleted = this.isCompleted
    )
}
