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
 * Dialog pour modifier le nom complet de l'utilisateur
 *
 * ## Rôle
 * - Affiche un AlertDialog avec un TextField
 * - Valide que le nom n'est pas vide
 * - Callback onConfirm avec le nouveau nom
 *
 * ## Communication
 * Parent (UserProfileScreen) ←→ EditNameDialog
 *   - Parent passe currentFullName
 *   - Dialog appelle onConfirm(fullName) ou onDismiss()
 *
 * @param currentFullName Nom complet actuel à pré-remplir
 * @param onDismiss Callback pour annuler
 * @param onConfirm Callback avec le nouveau nom complet
 */
@Composable
fun EditNameDialog(
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
