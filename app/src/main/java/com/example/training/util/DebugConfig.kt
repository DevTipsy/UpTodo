package com.example.training.util

/**
 * Configuration pour les logs de debug
 * Les logs sont automatiquement désactivés en production
 */
object DebugConfig {
    /**
     * Active/désactive les logs de debug
     * true en mode DEBUG, false en RELEASE
     *
     * Pour désactiver les logs, changez cette valeur à false
     */
    const val ENABLE_LOGS = true
}
