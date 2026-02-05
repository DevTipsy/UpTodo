package com.example.training.view.dialogs

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.training.R
import com.example.training.ui.theme.*

/**
 * Dialog pour modifier le mot de passe
 *
 * ## Rôle
 * - Affiche un AlertDialog avec TextField pour le nouveau mot de passe
 * - Valide longueur minimale (6 caractères)
 * - Callback onConfirm avec le nouveau mot de passe
 *
 * ## Communication
 * Parent (UserProfileScreen) ←→ EditPasswordDialog
 *   - Dialog appelle onConfirm(password) ou onDismiss()
 *
 * @param onDismiss Callback pour annuler
 * @param onConfirm Callback avec le nouveau mot de passe
 */
@Composable
fun EditPasswordDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
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
                        onConfirm(newPassword)
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
