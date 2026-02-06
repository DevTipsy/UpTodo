package com.example.training.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.training.view.AddTaskScreen
import com.example.training.view.IntroScreen
import com.example.training.view.LoginScreen
import com.example.training.view.OnboardingScreen
import com.example.training.view.RegisterScreen
import com.example.training.view.TaskDetailScreen
import com.example.training.view.UserProfileScreen
import com.example.training.view.WelcomeScreen
import com.example.training.viewmodel.AuthViewModel
import com.example.training.viewmodel.CategoryViewModel
import com.example.training.viewmodel.OnboardingViewModel
import com.example.training.navigation.Screen
import com.example.training.viewmodel.TaskViewModel
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
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Navigation de intro vers onboarding après 2s
    LaunchedEffect(Unit) {
        delay(2000L)
        navController.navigate(Screen.Onboarding.route) {
            popUpTo(Screen.Intro.route) { inclusive = true }
        }
    }

    Box {
        NavHost(
            navController = navController,
            startDestination = Screen.Intro.route,
            enterTransition = { EnterTransition.Companion.None },
            exitTransition = { ExitTransition.Companion.None },
            popEnterTransition = { EnterTransition.Companion.None },
            popExitTransition = { ExitTransition.Companion.None }
        ) {
            composable(Screen.Intro.route) {
                IntroScreen()
            }

            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    viewModel = onboardingViewModel,
                    onComplete = {
                        navController.navigate(Screen.Welcome.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Welcome.route) {
                WelcomeScreen(
                    onLogin = {
                        navController.navigate(Screen.Login.route) {
                            launchSingleTop = true
                        }
                    },
                    onRegister = {
                        navController.navigate(Screen.Register.route) {
                            launchSingleTop = true
                        }
                    },
                    onNavigateBack = {
                        onboardingViewModel.skipToEnd()
                        navController.navigate(Screen.Onboarding.route) {
                            popUpTo(Screen.Welcome.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Login.route) {
                LoginScreen(
                    navController = navController,
                    viewModel = authViewModel,
                    onNavigateBack = {
                        onboardingViewModel.skipToEnd()
                        navController.navigate(Screen.Onboarding.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    navController = navController,
                    viewModel = authViewModel,
                    onNavigateBack = {
                        onboardingViewModel.skipToEnd()
                        navController.navigate(Screen.Onboarding.route) {
                            popUpTo(Screen.Register.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Home.route) {
                AddTaskScreen(
                    authViewModel = authViewModel,
                    viewModel = taskViewModel,
                    categoryViewModel = categoryViewModel,
                    onAddTask = { showTaskDetail = true },
                    onNavigateToProfile = {
                        navController.navigate(Screen.UserProfile.route)
                    }
                )
            }

            composable(Screen.UserProfile.route) {
                UserProfileScreen(
                    navController = navController,
                    authViewModel = authViewModel,
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
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}