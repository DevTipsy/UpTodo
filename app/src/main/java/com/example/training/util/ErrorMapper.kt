package com.example.training.util

import androidx.annotation.StringRes
import com.example.training.R
import com.google.firebase.auth.FirebaseAuthException

/**
 * ErrorMapper est responsable de la conversion des exceptions en ressources de chaînes localisées.
 *
 * ## Rôle
 * - Transforme les codes d'erreur Firebase en messages utilisateur localisés
 * - Centralise la gestion des messages d'erreur
 * - Facilite l'internationalisation
 *
 * ## Communication
 * Repository → ErrorMapper → ViewModel → View
 *
 * ## Utilisation
 * ```kotlin
 * try {
 *     firebaseUser.updateEmail(newEmail).await()
 * } catch (e: FirebaseAuthException) {
 *     val messageRes = ErrorMapper.mapAuthError(e)
 *     // messageRes est un @StringRes Int (ex: R.string.error_email_already_in_use)
 *     return Result.Error(e, messageRes)
 * }
 * ```
 */
object ErrorMapper {

    /**
     * Mappe une exception Firebase Auth vers une ressource string.
     *
     * @param exception L'exception Firebase à mapper
     * @return Une référence @StringRes vers le message d'erreur approprié
     *
     * ## Flow
     * 1. Récupère le errorCode de l'exception Firebase
     * 2. Matche le code avec un message approprié dans strings.xml
     * 3. Retourne la référence R.string.xxx
     */
    @StringRes
    fun mapAuthError(exception: FirebaseAuthException): Int {
        return when (exception.errorCode) {
            // Erreurs d'email
            "ERROR_EMAIL_ALREADY_IN_USE" -> R.string.error_email_already_in_use
            "ERROR_INVALID_EMAIL" -> R.string.error_invalid_email

            // Erreurs de mot de passe
            "ERROR_WEAK_PASSWORD" -> R.string.error_weak_password
            "ERROR_WRONG_PASSWORD" -> R.string.error_wrong_password

            // Erreurs d'utilisateur
            "ERROR_USER_NOT_FOUND" -> R.string.error_user_not_found
            "ERROR_USER_DISABLED" -> R.string.error_user_disabled

            // Erreurs de session
            "ERROR_REQUIRES_RECENT_LOGIN" -> R.string.error_requires_recent_login

            // Erreur générique par défaut
            else -> R.string.error_generic
        }
    }

    /**
     * Mappe une exception générique vers une ressource string.
     * Utilisé pour les erreurs non-Firebase (réseau, Firestore, etc.)
     */
    @StringRes
    fun mapGenericError(exception: Exception, defaultMessageRes: Int = R.string.error_generic): Int {
        return when (exception) {
            is FirebaseAuthException -> mapAuthError(exception)
            // Ajouter d'autres types d'exceptions ici si nécessaire
            else -> defaultMessageRes
        }
    }
}
