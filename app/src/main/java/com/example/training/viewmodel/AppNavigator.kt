package com.example.training.viewmodel

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.training.view.AddTaskScreen
import com.example.training.view.IntroScreen
import com.example.training.view.LoginScreen
import com.example.training.view.OnboardingScreen
import com.example.training.view.RegisterScreen
import com.example.training.view.TaskDetailScreen
import com.example.training.view.WelcomeScreen
import kotlinx.coroutines.delay

enum class Screen {
    INTRO, ONBOARDING, WELCOME, LOGIN, REGISTER, HOME
}

@Composable
fun AppNavigator(
    authViewModel: AuthViewModel = viewModel(),
    onboardingViewModel: OnboardingViewModel = viewModel(),
    taskViewModel: TaskViewModel = viewModel()
) {
    var currentScreen by remember { mutableStateOf(Screen.INTRO) }
    var showTaskDetail by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(2000L)
        currentScreen = Screen.ONBOARDING
    }

    Box {
        when (currentScreen) {
            Screen.INTRO -> IntroScreen()
            Screen.ONBOARDING -> OnboardingScreen(
                viewModel = onboardingViewModel,
                onComplete = { currentScreen = Screen.WELCOME }
            )
            Screen.WELCOME -> WelcomeScreen(
                onLogin = { currentScreen = Screen.LOGIN },
                onRegister = { currentScreen = Screen.REGISTER },
                onNavigateBack = {
                    onboardingViewModel.skipToEnd()
                    currentScreen = Screen.ONBOARDING
                }
            )
            Screen.LOGIN -> LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = { currentScreen = Screen.REGISTER },
                onNavigateBack = {
                    onboardingViewModel.skipToEnd()
                    currentScreen = Screen.ONBOARDING
                },
                onLoginSuccess = { currentScreen = Screen.HOME }
            )
            Screen.REGISTER -> RegisterScreen(
                viewModel = authViewModel,
                onNavigateToLogin = { currentScreen = Screen.LOGIN },
                onNavigateBack = {
                    onboardingViewModel.skipToEnd()
                    currentScreen = Screen.ONBOARDING
                },
                onRegisterSuccess = { currentScreen = Screen.HOME }
            )
            Screen.HOME -> AddTaskScreen(
                onAddTask = { showTaskDetail = true }
            )
        }

        // Modal d'ajout de tâche
        if (showTaskDetail) {
            TaskDetailScreen(
                viewModel = taskViewModel,
                onDismiss = { showTaskDetail = false },
                onTaskAdded = {
                    showTaskDetail = false
                    taskViewModel.loadTasks()
                }
            )
        }
    }
}