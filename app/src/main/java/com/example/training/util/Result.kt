package com.example.training.util

import androidx.annotation.StringRes

/**
 * Sealed class représentant le résultat d'une opération asynchrone.
 *
 * ## Rôle
 * - Encapsule les états possibles d'une opération : succès, erreur, chargement
 * - Type-safe : force la gestion de tous les cas possibles
 *
 * ## Communication
 * Repository → ViewModel (via Result) → View (collectAsState)
 *
 * ## Utilisation
 * ```kotlin
 * // Dans le Repository
 * suspend fun updateEmail(email: String): Result<Unit> {
 *     return try {
 *         firestore.update("email", email).await()
 *         Result.Success(Unit)
 *     } catch (e: FirebaseAuthException) {
 *         Result.Error(e, ErrorMapper.mapAuthError(e))
 *     }
 * }
 *
 * // Dans le ViewModel
 * when (val result = repository.updateEmail(email)) {
 *     is Result.Success -> _uiEvent.send(UiEvent.ShowSnackbar(R.string.success_email_updated))
 *     is Result.Error -> _uiEvent.send(UiEvent.ShowSnackbar(result.messageRes))
 *     Result.Loading -> _isLoading.value = true
 * }
 * ```
 */
sealed class Result<out T> {
    /**
     * Représente un résultat réussi avec les données.
     * @param data Les données retournées par l'opération
     */
    data class Success<T>(val data: T) : Result<T>()

    /**
     * Représente une erreur avec l'exception et un message localisé.
     * @param exception L'exception qui s'est produite
     * @param messageRes Référence vers un string resource (@StringRes) pour le message d'erreur
     */
    data class Error(
        val exception: Exception,
        @StringRes val messageRes: Int? = null
    ) : Result<Nothing>()

    /**
     * Représente un état de chargement.
     * Utilisé pour afficher un loader/spinner dans l'UI
     */
    object Loading : Result<Nothing>()
}
