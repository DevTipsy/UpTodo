package com.example.training.view.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.training.R
import com.example.training.ui.theme.TextPrimary

/**
 * En-tête du profil avec bouton retour et titre
 *
 * ## Rôle
 * - Affiche le titre "Profil"
 * - Bouton de retour pour fermer l'écran
 *
 * ## Communication
 * Parent (UserProfileScreen) → ProfileHeader (callback onNavigateBack)
 *
 * @param onNavigateBack Callback appelé lors du clic sur le bouton retour
 */
@Composable
fun ProfileHeader(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
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
}
