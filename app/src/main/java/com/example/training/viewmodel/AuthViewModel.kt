package com.example.training.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.training.data.mapper.UserMapper.toDomain
import com.example.training.model.User
import com.example.training.repository.AuthRepository
import com.example.training.util.Result
import com.example.training.util.UiEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    // État : Utilisateur actuel (Domain Model)
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // État : Chargement
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Événements UI (navigation, snackbars)
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        // Charger l'utilisateur actuel au démarrage si connecté
        loadCurrentUser()
    }

    /**
     * Connexion
     */
    fun signIn(email: String, password: String) {
        // Validation locale (optionnelle)
        if (email.isBlank() || password.isBlank()) {
            viewModelScope.launch {
                _uiEvent.send(UiEvent.ShowSnackbar("Email et mot de passe requis"))
            }
            return
        }

        viewModelScope.launch {
            _isLoading.value = true

            when (val result = authRepository.signIn(email, password)) {
                is Result.Success -> {
                    // Convertir UserDto → User avec le mapper
                    _currentUser.value = result.data.toDomain()
                    _uiEvent.send(UiEvent.ShowSnackbar("Connexion réussie"))
                    _uiEvent.send(UiEvent.Navigate(Routes.HOME))
                }
                is Result.Error -> {
                    // Utiliser le message custom si disponible
                    val message = result.message ?: result.exception.message ?: "Erreur de connexion"
                    _uiEvent.send(UiEvent.ShowSnackbar(message))
                }
                Result.Loading -> { /* Pas utilisé ici */ }
            }

            _isLoading.value = false
        }
    }

    /**
     * Inscription
     */
    fun signUp(email: String, password: String, prenom: String, nom: String) {
        // Validation locale
        if (email.isBlank() || password.isBlank() || prenom.isBlank() || nom.isBlank()) {
            viewModelScope.launch {
                _uiEvent.send(UiEvent.ShowSnackbar("Tous les champs sont requis"))
            }
            return
        }

        if (password.length < 6) {
            viewModelScope.launch {
                _uiEvent.send(UiEvent.ShowSnackbar("Le mot de passe doit contenir au moins 6 caractères"))
            }
            return
        }

        viewModelScope.launch {
            _isLoading.value = true

            when (val result = authRepository.signUp(email, password, prenom, nom)) {
                is Result.Success -> {
                    // Convertir UserDto → User avec le mapper
                    _currentUser.value = result.data.toDomain()
                    _uiEvent.send(UiEvent.ShowSnackbar("Compte créé avec succès"))
                    _uiEvent.send(UiEvent.Navigate(Routes.HOME))
                }
                is Result.Error -> {
                    val message = result.message ?: result.exception.message ?: "Erreur d'inscription"
                    _uiEvent.send(UiEvent.ShowSnackbar(message))
                }
                Result.Loading -> {}
            }

            _isLoading.value = false
        }
    }

    /**
     * Charger l'utilisateur actuel depuis Firestore
     * Appelé au démarrage dans init {}
     */
    private fun loadCurrentUser() {
        viewModelScope.launch {
            val userDto = authRepository.getCurrentUser()
            _currentUser.value = userDto?.toDomain()
        }
    }

    /**
     * Déconnexion
     */
    fun signOut() {
        authRepository.signOut()
        _currentUser.value = null
        viewModelScope.launch {
            _uiEvent.send(UiEvent.ShowSnackbar("Déconnexion réussie"))
            _uiEvent.send(UiEvent.Navigate(Routes.LOGIN))
        }
    }
}
