package com.example.training

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.training.ui.theme.TrainingTheme
import com.example.training.view.LoginScreen
import com.example.training.view.OnboardingScreen
import com.example.training.view.RegisterScreen
import com.example.training.view.WelcomeScreen
import com.example.training.viewmodel.AuthViewModel
import com.example.training.viewmodel.OnboardingViewModel
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TrainingTheme {
                AppNavigator()
            }
        }
    }
}

enum class Screen {
    INTRO, ONBOARDING, WELCOME, LOGIN, REGISTER, HOME
}

@Composable
fun AppNavigator(
    authViewModel: AuthViewModel = viewModel(),
    onboardingViewModel: OnboardingViewModel = viewModel()
) {
    var currentScreen by remember { mutableStateOf(Screen.INTRO) }

    LaunchedEffect(Unit) {
        delay(2000L)
        currentScreen = Screen.ONBOARDING
    }

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
        Screen.HOME -> HomeScreen(
            viewModel = authViewModel,
            onLogout = { currentScreen = Screen.LOGIN }
        )
    }
}

@Composable
fun IntroScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "Logo UpTodo",
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "UpTodo",
            fontSize = 24.sp,
            color = Color.White
        )
    }
}

@Composable
fun HomeScreen(
    viewModel: AuthViewModel,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Bienvenue !",
            fontSize = 32.sp,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Email: ${viewModel.currentUser?.email}",
            fontSize = 16.sp,
            color = Color.White.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(40.dp))

        androidx.compose.material3.Button(
            onClick = {
                viewModel.signOut()
                onLogout()
            },
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = Color(0xFF8875FF)
            )
        ) {
            Text("Se déconnecter")
        }
    }
}

@Preview
@Composable
fun IntroScreenPreview() {
    TrainingTheme {
        IntroScreen()
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    TrainingTheme {
        HomeScreen(
            viewModel = viewModel(),
            onLogout = {}
        )
    }
}