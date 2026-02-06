package com.example.training.util

import java.util.Calendar

/**
 * Utilitaires pour la manipulation des dates
 */
object DateUtils {

    /**
     * Compare deux timestamps et vérifie s'ils correspondent au même jour (ignore l'heure)
     *
     * @param timestamp1 Premier timestamp en millisecondes
     * @param timestamp2 Deuxième timestamp en millisecondes
     * @return true si les deux timestamps sont le même jour, false sinon
     */
    fun isSameDay(timestamp1: Long, timestamp2: Long): Boolean {
        val cal1 = Calendar.getInstance().apply { timeInMillis = timestamp1 }
        val cal2 = Calendar.getInstance().apply { timeInMillis = timestamp2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
}
