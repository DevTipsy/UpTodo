package com.example.training.model

import androidx.annotation.DrawableRes
import com.example.training.R

data class Category(
    val id: String = "",
    val name: String = "",
    val iconName: String = "",
    val color: Long = 0L
) {
    // Mapping du nom d'icône vers le drawable
    @DrawableRes
    fun getIconDrawable(): Int {
        return when (iconName) {
            "grocery" -> R.drawable.category_grocery
            "work" -> R.drawable.category_work
            "sport" -> R.drawable.category_sport
            "design" -> R.drawable.category_design
            "university" -> R.drawable.category_university
            "social" -> R.drawable.category_social
            "music" -> R.drawable.category_music
            "health" -> R.drawable.category_health
            "movie" -> R.drawable.category_movie
            "home" -> R.drawable.category_home
            else -> R.drawable.category_work
        }
    }
}
