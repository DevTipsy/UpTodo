package com.example.training.util

import androidx.annotation.StringRes

/**
 * Sealed class représentant les événements UI que le ViewModel envoie à la View.
 *
 * ## Rôle
 * - Communication unidirectionnelle ViewModel → View
 * - Événements ponctuels (one-shot events) comme les snackbars, navigation
 *
 * ## Communication
 * ViewModel (_uiEvent.send()) → View (LaunchedEffect + collect)
 *
 * ## Utilisation
 * ```kotlin
 * // Dans le ViewModel
 * viewModelScope.launch {
 *     _uiEvent.send(UiEvent.ShowSnackbar(R.string.success_email_updated))
 * }
 *
 * // Dans le Composable
 * LaunchedEffect(viewModel) {
 *     viewModel.uiEvent.collect { event ->
 *         when (event) {
 *             is UiEvent.ShowSnackbar -> {
 *                 snackbarHostState.showSnackbar(context.getString(event.messageRes))
 *             }
 *             is UiEvent.Navigate -> navController.navigate(event.route)
 *             UiEvent.NavigateBack -> navController.popBackStack()
 *         }
 *     }
 * }
 * ```
 */
sealed class UiEvent {
    /**
     * Affiche un Snackbar avec un message localisé
     * @param messageRes Référence vers un string resource (@StringRes)
     */
    data class ShowSnackbar(@StringRes val messageRes: Int) : UiEvent()

    /**
     * Navigation vers une route
     * @param route La route de destination (ex: Routes.HOME)
     */
    data class Navigate(val route: String) : UiEvent()

    /**
     * Retour en arrière dans la navigation
     */
    object NavigateBack : UiEvent()
}
