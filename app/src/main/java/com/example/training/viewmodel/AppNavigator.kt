package com.example.training.viewmodel

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import com.example.training.view.AddTaskScreen
import com.example.training.view.IntroScreen
import com.example.training.view.LoginScreen
import com.example.training.view.OnboardingScreen
import com.example.training.view.RegisterScreen
import com.example.training.view.TaskDetailScreen
import com.example.training.view.WelcomeScreen
import kotlinx.coroutines.delay

@Composable
fun AppNavigator(
    authViewModel: AuthViewModel,
    onboardingViewModel: OnboardingViewModel,
    taskViewModel: TaskViewModel,
    categoryViewModel: CategoryViewModel
) {
    val navController = rememberNavController()
    var showTaskDetail by remember { mutableStateOf(false) }

    // Navigation de intro vers onboarding après 2s
    LaunchedEffect(Unit) {
        delay(2000L)
        navController.navigate(Routes.ONBOARDING) {
            popUpTo(Routes.INTRO) { inclusive = true }
        }
    }

    Box {
        NavHost(
            navController = navController,
            startDestination = Routes.INTRO,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            composable(Routes.INTRO) {
                IntroScreen()
            }

            composable(Routes.ONBOARDING) {
                OnboardingScreen(
                    viewModel = onboardingViewModel,
                    onComplete = {
                        navController.navigate(Routes.WELCOME) {
                            popUpTo(Routes.ONBOARDING) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.WELCOME) {
                WelcomeScreen(
                    onLogin = {
                        navController.navigate(Routes.LOGIN) {
                            launchSingleTop = true
                        }
                    },
                    onRegister = {
                        navController.navigate(Routes.REGISTER) {
                            launchSingleTop = true
                        }
                    },
                    onNavigateBack = {
                        onboardingViewModel.skipToEnd()
                        navController.navigate(Routes.ONBOARDING) {
                            popUpTo(Routes.WELCOME) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.LOGIN) {
                LoginScreen(
                    viewModel = authViewModel,
                    onNavigateToRegister = {
                        navController.navigate(Routes.REGISTER) {
                            popUpTo(Routes.WELCOME)
                            launchSingleTop = true
                        }
                    },
                    onNavigateBack = {
                        onboardingViewModel.skipToEnd()
                        navController.navigate(Routes.ONBOARDING) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    },
                    onLoginSuccess = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.REGISTER) {
                RegisterScreen(
                    viewModel = authViewModel,
                    onNavigateToLogin = {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.WELCOME)
                            launchSingleTop = true
                        }
                    },
                    onNavigateBack = {
                        onboardingViewModel.skipToEnd()
                        navController.navigate(Routes.ONBOARDING) {
                            popUpTo(Routes.REGISTER) { inclusive = true }
                        }
                    },
                    onRegisterSuccess = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.HOME) {
                AddTaskScreen(
                    onAddTask = { showTaskDetail = true }
                )
            }
        }

        // Modal d'ajout de tâche
        if (showTaskDetail) {
            TaskDetailScreen(
                viewModel = taskViewModel,
                categoryViewModel = categoryViewModel,
                onDismiss = { showTaskDetail = false },
                onTaskAdded = {
                    showTaskDetail = false
                    taskViewModel.loadTasks()
                }
            )
        }
    }
}
