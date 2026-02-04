package com.example.training.data.dto

data class UserDto(
    val id: String,
    val prenom: String,
    val nom: String,
    val email: String,
    val photoUrl: String?
) {
    // Constructeur vide requis par Firestore
    constructor() : this("", "", "", "", null)
}
