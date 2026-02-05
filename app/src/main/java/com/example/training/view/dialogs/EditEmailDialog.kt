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
 * Dialog pour modifier l'email
 *
 * ## Rôle
 * - Affiche un AlertDialog avec TextField pour le nouvel email
 * - Valide format email (contient @)
 * - Callback onConfirm avec le nouvel email
 *
 * ## Communication
 * Parent (UserProfileScreen) ←→ EditEmailDialog
 *   - Parent passe currentEmail
 *   - Dialog appelle onConfirm(email) ou onDismiss()
 *
 * @param currentEmail Email actuel à pré-remplir
 * @param onDismiss Callback pour annuler
 * @param onConfirm Callback avec le nouvel email
 */
@Composable
fun EditEmailDialog(
    currentEmail: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
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
                        onConfirm(newEmail)
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
