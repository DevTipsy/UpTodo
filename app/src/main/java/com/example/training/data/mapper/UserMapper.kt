package com.example.training.data.mapper

import com.example.training.data.dto.UserDto
import com.example.training.model.User

fun UserDto.toDomain(): User {
    return User(
        id = this.id,
        prenom = this.prenom,
        nom = this.nom,
        email = this.email,
        photoUrl = this.photoUrl
    )
}

fun User.toDto(): UserDto {
    return UserDto(
        id = this.id,
        prenom = this.prenom,
        nom = this.nom,
        email = this.email,
        photoUrl = this.photoUrl
    )
}
