package com.example.training.viewmodel  // ← minuscule !

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.training.R
import com.example.training.model.OnboardingPage

class OnboardingViewModel : ViewModel() {
    private val pages = listOf(
        OnboardingPage(
            imageRes = R.drawable.onboarding1,
            title = "Manage your tasks",
            description = "You can easily manage all of your daily tasks in DoMe for free"
        ),
        OnboardingPage(
            imageRes = R.drawable.onboarding2,
            title = "Create daily routine",
            description = "In Uptodo you can create your personalized routine to stay productive"
        ),
        OnboardingPage(
            imageRes = R.drawable.onboarding3,
            title = "Organize your tasks",
            description = "You can organize your daily tasks by adding your tasks into separate categories"
        )
    )

    fun previousPage() {
        if (currentPage > 0) {
            currentPage--
        }
    }

    var currentPage by mutableStateOf(0)
        private set

    val currentPageData: OnboardingPage
        get() = pages[currentPage]

    val isLastPage: Boolean
        get() = currentPage == pages.size - 1

    val pageCount: Int
        get() = pages.size

    fun nextPage() {
        if (currentPage < pages.size - 1) {
            currentPage++
        }
    }

    fun skipToEnd() {
        currentPage = pages.size - 1
    }
}