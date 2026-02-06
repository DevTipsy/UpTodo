package com.example.training.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.training.R
import com.example.training.data.mapper.UserMapper.toDomain
import com.example.training.model.User
import com.example.training.navigation.Screen
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
                _uiEvent.send(UiEvent.ShowSnackbar(R.string.validation_email_password_required))
            }
            return
        }

        viewModelScope.launch {
            _isLoading.value = true

            when (val result = authRepository.signIn(email, password)) {
                is Result.Success -> {
                    // Convertir UserDto → User avec le mapper
                    _currentUser.value = result.data.toDomain()
                    _uiEvent.send(UiEvent.ShowSnackbar(R.string.success_login))
                    _uiEvent.send(UiEvent.Navigate(Screen.Home.route))
                }
                is Result.Error -> {
                    val messageRes = result.messageRes ?: R.string.error_connection
                    _uiEvent.send(UiEvent.ShowSnackbar(messageRes))
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
                _uiEvent.send(UiEvent.ShowSnackbar(R.string.validation_all_fields_required))
            }
            return
        }

        if (password.length < 6) {
            viewModelScope.launch {
                _uiEvent.send(UiEvent.ShowSnackbar(R.string.validation_password_min_6))
            }
            return
        }

        viewModelScope.launch {
            _isLoading.value = true

            when (val result = authRepository.signUp(email, password, prenom, nom)) {
                is Result.Success -> {
                    // Convertir UserDto → User avec le mapper
                    _currentUser.value = result.data.toDomain()
                    _uiEvent.send(UiEvent.ShowSnackbar(R.string.success_account_created))
                    _uiEvent.send(UiEvent.Navigate(Screen.Home.route))
                }
                is Result.Error -> {
                    val messageRes = result.messageRes ?: R.string.error_signup
                    _uiEvent.send(UiEvent.ShowSnackbar(messageRes))
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
            _uiEvent.send(UiEvent.ShowSnackbar(R.string.success_logout))
            _uiEvent.send(UiEvent.Navigate(Screen.Login.route))
        }
    }

    /**
     * Mettre à jour le nom de l'utilisateur
     */
    fun updateUserName(prenom: String, nom: String) {
        if (prenom.isBlank() || nom.isBlank()) {
            viewModelScope.launch {
                _uiEvent.send(UiEvent.ShowSnackbar(R.string.validation_name_required))
            }
            return
        }

        viewModelScope.launch {
            _isLoading.value = true

            when (val result = authRepository.updateUserName(prenom, nom)) {
                is Result.Success -> {
                    // Recharger l'utilisateur pour mettre à jour l'état
                    loadCurrentUser()
                    _uiEvent.send(UiEvent.ShowSnackbar(R.string.success_name_updated))
                }
                is Result.Error -> {
                    val messageRes = result.messageRes ?: R.string.error_generic
                    _uiEvent.send(UiEvent.ShowSnackbar(messageRes))
                }
                Result.Loading -> {}
            }

            _isLoading.value = false
        }
    }

    /**
     * Mettre à jour l'email de l'utilisateur
     */
    fun updateUserEmail(newEmail: String) {
        if (newEmail.isBlank()) {
            viewModelScope.launch {
                _uiEvent.send(UiEvent.ShowSnackbar(R.string.validation_email_required))
            }
            return
        }

        viewModelScope.launch {
            _isLoading.value = true

            when (val result = authRepository.updateUserEmail(newEmail)) {
                is Result.Success -> {
                    loadCurrentUser()
                    _uiEvent.send(UiEvent.ShowSnackbar(R.string.success_email_updated))
                }
                is Result.Error -> {
                    val messageRes = result.messageRes ?: R.string.error_generic
                    _uiEvent.send(UiEvent.ShowSnackbar(messageRes))
                }
                Result.Loading -> {}
            }

            _isLoading.value = false
        }
    }

    /**
     * Mettre à jour le mot de passe de l'utilisateur
     */
    fun updateUserPassword(newPassword: String) {
        if (newPassword.isBlank()) {
            viewModelScope.launch {
                _uiEvent.send(UiEvent.ShowSnackbar(R.string.validation_password_required))
            }
            return
        }

        if (newPassword.length < 6) {
            viewModelScope.launch {
                _uiEvent.send(UiEvent.ShowSnackbar(R.string.error_weak_password))
            }
            return
        }

        viewModelScope.launch {
            _isLoading.value = true

            when (val result = authRepository.updateUserPassword(newPassword)) {
                is Result.Success -> {
                    _uiEvent.send(UiEvent.ShowSnackbar(R.string.success_password_updated))
                }
                is Result.Error -> {
                    val messageRes = result.messageRes ?: R.string.error_generic
                    _uiEvent.send(UiEvent.ShowSnackbar(messageRes))
                }
                Result.Loading -> {}
            }

            _isLoading.value = false
        }
    }
}
