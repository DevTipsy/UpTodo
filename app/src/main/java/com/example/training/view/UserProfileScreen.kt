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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.training.R
import com.example.training.ui.theme.*
import com.example.training.util.UiEvent
import com.example.training.viewmodel.AuthViewModel
import com.example.training.viewmodel.Screen
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
    val context = LocalContext.current

    var showEditNameDialog by remember { mutableStateOf(false) }
    var showEditPasswordDialog by remember { mutableStateOf(false) }
    var showEditEmailDialog by remember { mutableStateOf(false) }

    // Observer les événements UI du ViewModel
    LaunchedEffect(authViewModel) {
        authViewModel?.uiEvent?.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    scope.launch {
                        // Convertir le @StringRes en String
                        val message = context.getString(event.messageRes)
                        snackbarHostState.showSnackbar(message)
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
            .background(AppBackground)
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
                        contentDescription = stringResource(R.string.retour),
                        tint = TextPrimary
                    )
                }
                Text(
                    text = stringResource(R.string.profil),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Prénom et Nom de l'utilisateur
            Text(
                text = "${currentUser?.prenom ?: ""} ${currentUser?.nom ?: ""}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = currentUser?.email ?: "",
                fontSize = 14.sp,
                color = TextSecondary,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Options de modification
            ProfileOption(
                title = stringResource(R.string.modifier_nom),
                onClick = { showEditNameDialog = true }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ProfileOption(
                title = stringResource(R.string.modifier_mot_de_passe),
                onClick = { showEditPasswordDialog = true }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ProfileOption(
                title = stringResource(R.string.modifier_email),
                onClick = { showEditEmailDialog = true }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Bouton de déconnexion
            Button(
                onClick = { authViewModel?.signOut() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ErrorRed
                )
            ) {
                Text(
                    text = stringResource(R.string.deconnexion),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
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
            currentFullName = "${currentUser?.prenom ?: ""} ${currentUser?.nom ?: ""}".trim(),
            onDismiss = { showEditNameDialog = false },
            onConfirm = { fullName ->
                // Séparer le nom complet en prénom et nom
                val parts = fullName.trim().split(" ", limit = 2)
                val prenom = parts.getOrNull(0) ?: ""
                val nom = parts.getOrNull(1) ?: ""
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
            containerColor = AppSurface
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
                color = TextPrimary
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = TextSecondary
            )
        }
    }
}

@Composable
private fun EditNameDialog(
    currentFullName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var fullName by remember { mutableStateOf(currentFullName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.modifier_nom),
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text(stringResource(R.string.nouveau_nom_complet)) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = AppPrimary,
                    unfocusedBorderColor = BorderGray,
                    focusedLabelColor = AppPrimary,
                    unfocusedLabelColor = BorderGray
                ),
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (fullName.isNotBlank()) {
                        onConfirm(fullName)
                    }
                }
            ) {
                Text(stringResource(R.string.confirmer), color = AppPrimary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.annuler), color = BorderGray)
            }
        },
        containerColor = AppSurface
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
                text = stringResource(R.string.modifier_mot_de_passe),
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text(stringResource(R.string.nouveau_mot_de_passe)) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = AppPrimary,
                    unfocusedBorderColor = BorderGray,
                    focusedLabelColor = AppPrimary,
                    unfocusedLabelColor = BorderGray
                ),
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (newPassword.isNotBlank() && newPassword.length >= 6) {
                        onConfirm("", newPassword)
                    }
                }
            ) {
                Text(stringResource(R.string.confirmer), color = AppPrimary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.annuler), color = BorderGray)
            }
        },
        containerColor = AppSurface
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
                text = stringResource(R.string.modifier_email),
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            OutlinedTextField(
                value = newEmail,
                onValueChange = { newEmail = it },
                label = { Text(stringResource(R.string.nouvel_email)) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = AppPrimary,
                    unfocusedBorderColor = BorderGray,
                    focusedLabelColor = AppPrimary,
                    unfocusedLabelColor = BorderGray
                ),
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (newEmail.isNotBlank() && newEmail.contains("@")) {
                        onConfirm(newEmail, "")
                    }
                }
            ) {
                Text(stringResource(R.string.confirmer), color = AppPrimary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.annuler), color = BorderGray)
            }
        },
        containerColor = AppSurface
    )
}

@Preview
@Composable
private fun UserProfileScreenPreview() {
    TrainingTheme {
        UserProfileScreen()
    }
}
