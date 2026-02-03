package com.example.training.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Palette de couleurs principale de l'application UpTodo
 * Ces couleurs sont utilisées dans tout le projet pour garantir la cohérence visuelle
 */

// === Couleurs principales ===
/** Couleur primaire de l'app - Violet/Purple utilisé pour les actions principales */
val AppPrimary = Color(0xFF8875FF)

/** Fond principal de l'app - Noir */
val AppBackground = Color(0xFF000000)

/** Surface sombre utilisée pour les cards, dialogs, etc. */
val AppSurface = Color(0xFF1D1D1D)

// === Couleurs de texte ===
/** Texte blanc principal */
val TextPrimary = Color(0xFFFFFFFF)

/** Texte secondaire avec opacité (60%) */
val TextSecondary = Color(0x99FFFFFF) // White avec 60% alpha

/** Texte tertiaire avec opacité (30%) */
val TextTertiary = Color(0x4DFFFFFF) // White avec 30% alpha

// === Couleurs d'état ===
/** Couleur pour les erreurs et actions destructives */
val ErrorRed = Color(0xFFFF4444)

/** Couleur de succès */
val SuccessGreen = Color(0xFF4CAF50)

/** Couleur d'avertissement */
val WarningOrange = Color(0xFFFF9800)

// === Couleurs de bordure ===
/** Bordure standard grise */
val BorderGray = Color(0xFF808080)

/** Bordure avec opacité (30%) */
val BorderLight = Color(0x4DFFFFFF)

// === Anciennes couleurs Material (conservées pour compatibilité) ===
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)