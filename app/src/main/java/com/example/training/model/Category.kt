package com.example.training.model

import androidx.annotation.DrawableRes
import com.example.training.R

data class Category(
    val id: String,
    val name: String,
    val iconName: String, // Nom de l'icône pour Firestore (ex: "grocery", "work")
    val color: Long
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
            else -> R.drawable.category_work // Icône par défaut
        }
    }
}

object Categories {
    // Catégories par défaut (pour migration vers Firestore)
    val default = listOf(
        Category("", "Grocery", "grocery", 0xFFCCFF80),
        Category("", "Work", "work", 0xFFFF9680),
        Category("", "Sport", "sport", 0xFF80FFFF),
        Category("", "Design", "design", 0xFF80FFD9),
        Category("", "University", "university", 0xFF809CFF),
        Category("", "Social", "social", 0xFFFF80EB),
        Category("", "Music", "music", 0xFFFC80FF),
        Category("", "Health", "health", 0xFF80FFA3),
        Category("", "Movie", "movie", 0xFFFF80AF),
        Category("", "Home", "home", 0xFFFFCC80)
    )
}
