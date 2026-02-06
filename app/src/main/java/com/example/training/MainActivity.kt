package com.example.training

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.training.ui.theme.TrainingTheme
import com.example.training.navigation.AppNavigator
import com.example.training.viewmodel.AuthViewModel
import com.example.training.viewmodel.CategoryViewModel
import com.example.training.viewmodel.OnboardingViewModel
import com.example.training.viewmodel.TaskViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TrainingTheme {
                // ViewModels au niveau de l'activité pour éviter les recréations
                val authViewModel: AuthViewModel = viewModel()
                val onboardingViewModel: OnboardingViewModel = viewModel()
                val taskViewModel: TaskViewModel = viewModel()
                val categoryViewModel: CategoryViewModel = viewModel()

                AppNavigator(
                    authViewModel = authViewModel,
                    onboardingViewModel = onboardingViewModel,
                    taskViewModel = taskViewModel,
                    categoryViewModel = categoryViewModel
                )
            }
        }
    }
}