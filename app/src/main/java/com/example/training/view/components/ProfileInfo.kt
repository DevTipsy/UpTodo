package com.example.training.view.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.training.ui.theme.TextPrimary
import com.example.training.ui.theme.TextSecondary

/**
 * Affiche les informations de l'utilisateur (nom complet et email)
 *
 * ## Rôle
 * - Affiche le nom complet (prénom + nom)
 * - Affiche l'email avec style secondaire
 *
 * ## Communication
 * Parent (UserProfileScreen) → ProfileInfo (props: fullName, email)
 *
 * @param fullName Nom complet de l'utilisateur
 * @param email Email de l'utilisateur
 */
@Composable
fun ProfileInfo(
    fullName: String,
    email: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Nom complet
        Text(
            text = fullName,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Email
        Text(
            text = email,
            fontSize = 14.sp,
            color = TextSecondary,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
