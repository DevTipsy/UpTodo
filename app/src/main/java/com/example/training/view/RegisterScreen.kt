package com.example.training.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.training.ui.theme.TrainingTheme
import com.example.training.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel = viewModel(),
    onNavigateToLogin: () -> Unit,
    onNavigateBack: (() -> Unit)? = null,
    onRegisterSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Inscription",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

        Spacer(modifier = Modifier.height(40.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF8875FF),
                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedLabelColor = Color.White.copy(alpha = 0.6f),
                unfocusedLabelColor = Color.White.copy(alpha = 0.6f)
            ),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = null
            },
            label = { Text("Mot de passe") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF8875FF),
                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedLabelColor = Color.White.copy(alpha = 0.6f),
                unfocusedLabelColor = Color.White.copy(alpha = 0.6f)
            ),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                passwordError = null
            },
            label = { Text("Confirmer mot de passe") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF8875FF),
                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedLabelColor = Color.White.copy(alpha = 0.6f),
                unfocusedLabelColor = Color.White.copy(alpha = 0.6f)
            ),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (passwordError != null) {
            Text(
                text = passwordError ?: "",
                color = Color.Red,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (viewModel.errorMessage != null) {
            Text(
                text = viewModel.errorMessage ?: "",
                color = Color.Red,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = {
                when {
                    password != confirmPassword -> passwordError = "Les mots de passe ne correspondent pas"
                    password.length < 6 -> passwordError = "Minimum 6 caractères"
                    else -> viewModel.signUp(email, password, onRegisterSuccess)
                }
            },
            enabled = !viewModel.isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF8875FF)
            ),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text("S'inscrire", fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row {
            Text(
                text = "Déjà un compte ? ",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 14.sp
            )
            Text(
                text = "Se connecter",
                color = Color(0xFF8875FF),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onNavigateToLogin() }
            )
        }
    }

        if (onNavigateBack != null) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Retour",
                    tint = Color.White
                )
            }
        }
    }
}

@Preview
@Composable
fun RegisterScreenPreview() {
    TrainingTheme {
        RegisterScreen(
            onNavigateToLogin = {},
            onNavigateBack = {},
            onRegisterSuccess = {}
        )
    }
}
