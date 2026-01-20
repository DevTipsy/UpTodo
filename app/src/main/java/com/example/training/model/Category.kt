package com.example.training.model

import androidx.annotation.DrawableRes
import com.example.training.R

data class Category(
    val name: String,
    @DrawableRes val icon: Int,
    val color: Long = 0xFF8687E7
)

object Categories {
    val all = listOf(
        Category("Grocery", R.drawable.category_grocery, 0xFFCCFF80),
        Category("Work", R.drawable.category_work, 0xFFFF9680),
        Category("Sport", R.drawable.category_sport, 0xFF80FFFF),
        Category("Design", R.drawable.category_design, 0xFF80FFD9),
        Category("University", R.drawable.category_university, 0xFF809CFF),
        Category("Social", R.drawable.category_social, 0xFFFF80EB),
        Category("Music", R.drawable.category_music, 0xFFFC80FF),
        Category("Health", R.drawable.category_health, 0xFF80FFA3),
        Category("Movie", R.drawable.category_movie, 0xFFFF80AF),
        Category("Home", R.drawable.category_home, 0xFFFFCC80)
    )
}
