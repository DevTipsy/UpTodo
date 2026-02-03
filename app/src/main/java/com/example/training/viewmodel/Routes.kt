package com.example.training.viewmodel

/**
 * Sealed class définissant toutes les routes de navigation de l'application.
 *
 * ## Rôle
 * - Définit de manière type-safe toutes les destinations de navigation
 * - Permet de passer des paramètres typés (pas de String parsing)
 * - Facilite le refactoring (renommer une route met à jour tous les usages)
 *
 * ## Communication
 * Screen → NavController.navigate(Screen.xxx.route) → Destination
 *
 * ## Utilisation
 * ```kotlin
 * // Sans paramètres
 * navController.navigate(Screen.Login.route)
 *
 * // Avec paramètres (futur usage pour TaskDetail par exemple)
 * data class TaskDetail(val taskId: String) : Screen("task/{taskId}") {
 *     fun createRoute() = "task/$taskId"
 * }
 * navController.navigate(Screen.TaskDetail("123").createRoute())
 * ```
 *
 * ## Avantages
 * ✅ Type-safe : Impossible de passer une route invalide
 * ✅ Autocompletion IDE
 * ✅ Paramètres typés
 * ✅ Compile-time checking (erreurs détectées à la compilation)
 */
sealed class Screen(val route: String) {
    /**
     * Écran d'introduction (splash screen)
     */
    object Intro : Screen("intro")

    /**
     * Écran d'onboarding (premier lancement)
     */
    object Onboarding : Screen("onboarding")

    /**
     * Écran de bienvenue (après onboarding)
     */
    object Welcome : Screen("welcome")

    /**
     * Écran de connexion
     */
    object Login : Screen("login")

    /**
     * Écran d'inscription
     */
    object Register : Screen("register")

    /**
     * Écran principal (liste des tâches)
     */
    object Home : Screen("home")

    /**
     * Écran de profil utilisateur
     */
    object UserProfile : Screen("user_profile")

    // Exemple pour futur usage avec paramètres :
    // data class TaskDetail(val taskId: String) : Screen("task/{taskId}") {
    //     fun createRoute() = "task/$taskId"
    // }
}

/**
 * Objet Routes maintenu pour compatibilité ascendante
 * DEPRECATED : Utiliser Screen à la place
 */
@Deprecated(
    message = "Utiliser Screen sealed class à la place",
    replaceWith = ReplaceWith("Screen"),
    level = DeprecationLevel.WARNING
)
object Routes {
    const val INTRO = "intro"
    const val ONBOARDING = "onboarding"
    const val WELCOME = "welcome"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val USER_PROFILE = "user_profile"
}
