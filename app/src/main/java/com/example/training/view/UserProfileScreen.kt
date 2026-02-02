package com.example.training.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.training.ui.theme.TrainingTheme
import com.example.training.util.UiEvent
import com.example.training.viewmodel.AuthViewModel
import com.example.training.viewmodel.Routes
import kotlinx.coroutines.launch

@Composable
fun UserProfileScreen(
    navController: NavController = rememberNavController(),
    authViewModel: AuthViewModel? = null,
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val currentUser by authViewModel?.currentUser?.collectAsState() ?: return
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showEditNameDialog by remember { mutableStateOf(false) }
    var showEditPasswordDialog by remember { mutableStateOf(false) }
    var showEditEmailDialog by remember { mutableStateOf(false) }
    var showEditImageDialog by remember { mutableStateOf(false) }

    // Observer les événements UI du ViewModel
    LaunchedEffect(authViewModel) {
        authViewModel?.uiEvent?.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(event.message)
                    }
                }
                is UiEvent.Navigate -> {
                    // Rediriger vers LOGIN lors de la déconnexion
                    navController.navigate(event.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
                else -> {}
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 32.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            // Avatar en haut aligné à droite
            Avatar()

            Spacer(modifier = Modifier.height(24.dp))

            // Header avec bouton retour
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Retour",
                        tint = Color.White
                    )
                }
                Text(
                    text = "Profil",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Prénom et Nom de l'utilisateur
            Text(
                text = "${currentUser?.prenom ?: ""} ${currentUser?.nom ?: ""}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = currentUser?.email ?: "",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Options de modification
            ProfileOption(
                title = "Modifier le nom",
                onClick = { showEditNameDialog = true }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ProfileOption(
                title = "Modifier le mot de passe",
                onClick = { showEditPasswordDialog = true }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ProfileOption(
                title = "Modifier l'email",
                onClick = { showEditEmailDialog = true }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ProfileOption(
                title = "Modifier l'image",
                onClick = { showEditImageDialog = true }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Bouton de déconnexion
            Button(
                onClick = { authViewModel?.signOut() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red.copy(alpha = 0.8f)
                )
            ) {
                Text(
                    text = "Déconnexion",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // Snackbar pour les toasts
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }

    // Modales
    if (showEditNameDialog) {
        EditNameDialog(
            currentPrenom = currentUser?.prenom ?: "",
            currentNom = currentUser?.nom ?: "",
            onDismiss = { showEditNameDialog = false },
            onConfirm = { prenom, nom ->
                authViewModel?.updateUserName(prenom, nom)
                showEditNameDialog = false
            }
        )
    }

    if (showEditPasswordDialog) {
        EditPasswordDialog(
            onDismiss = { showEditPasswordDialog = false },
            onConfirm = { _, newPassword ->
                authViewModel?.updateUserPassword(newPassword)
                showEditPasswordDialog = false
            }
        )
    }

    if (showEditEmailDialog) {
        EditEmailDialog(
            currentEmail = currentUser?.email ?: "",
            onDismiss = { showEditEmailDialog = false },
            onConfirm = { newEmail, _ ->
                authViewModel?.updateUserEmail(newEmail)
                showEditEmailDialog = false
            }
        )
    }
}

@Composable
private fun ProfileOption(
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1D1D1D)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                color = Color.White
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun EditNameDialog(
    currentPrenom: String,
    currentNom: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var prenom by remember { mutableStateOf(currentPrenom) }
    var nom by remember { mutableStateOf(currentNom) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Modifier le nom",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = prenom,
                    onValueChange = { prenom = it },
                    label = { Text("Prénom") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF8875FF),
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = Color(0xFF8875FF),
                        unfocusedLabelColor = Color.Gray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = nom,
                    onValueChange = { nom = it },
                    label = { Text("Nom") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF8875FF),
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = Color(0xFF8875FF),
                        unfocusedLabelColor = Color.Gray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (prenom.isNotBlank() && nom.isNotBlank()) {
                        onConfirm(prenom, nom)
                    }
                }
            ) {
                Text("Confirmer", color = Color(0xFF8875FF))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler", color = Color.Gray)
            }
        },
        containerColor = Color(0xFF1D1D1D)
    )
}

@Composable
private fun EditPasswordDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var newPassword by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Modifier le mot de passe",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("Nouveau mot de passe") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF8875FF),
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = Color(0xFF8875FF),
                        unfocusedLabelColor = Color.Gray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (newPassword.isNotBlank() && newPassword.length >= 6) {
                        onConfirm("", newPassword)
                    }
                }
            ) {
                Text("Confirmer", color = Color(0xFF8875FF))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler", color = Color.Gray)
            }
        },
        containerColor = Color(0xFF1D1D1D)
    )
}

@Composable
private fun EditEmailDialog(
    currentEmail: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var newEmail by remember { mutableStateOf(currentEmail) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Modifier l'email",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = newEmail,
                    onValueChange = { newEmail = it },
                    label = { Text("Nouvel email") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF8875FF),
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = Color(0xFF8875FF),
                        unfocusedLabelColor = Color.Gray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (newEmail.isNotBlank() && newEmail.contains("@")) {
                        onConfirm(newEmail, "")
                    }
                }
            ) {
                Text("Confirmer", color = Color(0xFF8875FF))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler", color = Color.Gray)
            }
        },
        containerColor = Color(0xFF1D1D1D)
    )
}

@Preview
@Composable
private fun UserProfileScreenPreview() {
    TrainingTheme {
        UserProfileScreen()
    }
}
