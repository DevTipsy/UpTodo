package com.example.training.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.*

class CalendarViewModel : ViewModel() {
    // Date actuellement sélectionnée
    var selectedDate by mutableStateOf(Calendar.getInstance())
        private set

    // Liste des 7 jours à afficher (semaine courante autour de selectedDate)
    var weekDays by mutableStateOf<List<Calendar>>(emptyList())
        private set

    init {
        updateWeekDays()
    }

    // Sélectionner une date
    fun selectDate(date: Calendar) {
        selectedDate = date.clone() as Calendar
        updateWeekDays()
    }

    // Aller au jour précédent
    fun goToPreviousDay() {
        selectedDate = (selectedDate.clone() as Calendar).apply {
            add(Calendar.DAY_OF_MONTH, -1)
        }
        updateWeekDays()
    }

    // Aller au jour suivant
    fun goToNextDay() {
        selectedDate = (selectedDate.clone() as Calendar).apply {
            add(Calendar.DAY_OF_MONTH, 1)
        }
        updateWeekDays()
    }

    // Mettre à jour la liste des 7 jours (3 avant + jour actuel + 3 après)
    private fun updateWeekDays() {
        val days = mutableListOf<Calendar>()
        for (i in -3..3) {
            val day = (selectedDate.clone() as Calendar).apply {
                add(Calendar.DAY_OF_MONTH, i)
            }
            days.add(day)
        }
        weekDays = days
    }

    // Formater la date du header (-> "Mercredi 22 Janvier 2026")
    fun getFormattedHeaderDate(): String {
        val locale = Locale("fr", "FR")
        val dayName = SimpleDateFormat("EEEE", locale).format(selectedDate.time)
        val dayNumber = selectedDate.get(Calendar.DAY_OF_MONTH)
        val monthName = SimpleDateFormat("MMMM", locale).format(selectedDate.time)
        val year = selectedDate.get(Calendar.YEAR)
        return "${dayName.replaceFirstChar { it.uppercase() }} $dayNumber ${monthName.replaceFirstChar { it.uppercase() }} $year"
    }

    // Vérifier si deux dates sont le même jour
    fun isSameDay(date1: Calendar, date2: Calendar): Boolean {
        return date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR) &&
                date1.get(Calendar.DAY_OF_YEAR) == date2.get(Calendar.DAY_OF_YEAR)
    }
}
