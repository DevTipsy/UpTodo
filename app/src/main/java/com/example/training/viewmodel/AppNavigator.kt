package com.example.training.viewmodel

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
import com.example.training.view.UserProfileScreen
import com.example.training.view.WelcomeScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AppNavigator(
    authViewModel: AuthViewModel,
    onboardingViewModel: OnboardingViewModel,
    taskViewModel: TaskViewModel,
    categoryViewModel: CategoryViewModel
) {
    val navController = rememberNavController()
    var showTaskDetail by remember { mutableStateOf(false) }
    val snackbarHostState = remember { androidx.compose.material3.SnackbarHostState() }
    val scope = rememberCoroutineScope()

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
                    navController = navController,
                    viewModel = authViewModel,
                    onNavigateBack = {
                        onboardingViewModel.skipToEnd()
                        navController.navigate(Routes.ONBOARDING) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.REGISTER) {
                RegisterScreen(
                    navController = navController,
                    viewModel = authViewModel,
                    onNavigateBack = {
                        onboardingViewModel.skipToEnd()
                        navController.navigate(Routes.ONBOARDING) {
                            popUpTo(Routes.REGISTER) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.HOME) {
                AddTaskScreen(
                    authViewModel = authViewModel,
                    viewModel = taskViewModel,
                    categoryViewModel = categoryViewModel,
                    onAddTask = { showTaskDetail = true },
                    onNavigateToProfile = {
                        navController.navigate(Routes.USER_PROFILE)
                    }
                )
            }

            composable(Routes.USER_PROFILE) {
                UserProfileScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }

        // Modal d'ajout de tâche
        if (showTaskDetail) {
            TaskDetailScreen(
                authViewModel = authViewModel,
                viewModel = taskViewModel,
                categoryViewModel = categoryViewModel,
                onDismiss = { showTaskDetail = false },
                onTaskAdded = {
                    showTaskDetail = false
                }
            )
        }

        // Snackbar pour les messages
        androidx.compose.material3.SnackbarHost(
            hostState = snackbarHostState,
            modifier = androidx.compose.ui.Modifier
                .align(androidx.compose.ui.Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}
