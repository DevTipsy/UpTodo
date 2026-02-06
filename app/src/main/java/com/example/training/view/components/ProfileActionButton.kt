package com.example.training.view.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.training.ui.theme.AppSurface
import com.example.training.ui.theme.TextPrimary
import com.example.training.ui.theme.TextSecondary

/**
 * Bouton d'action du profil (Modifier le nom, email, mot de passe, etc.)
 *
 * ## Rôle
 * - Affiche un bouton cliquable avec icône de flèche
 * - Design de Card pour cohérence visuelle
 *
 * ## Communication
 * Parent → ProfileActionButton (props: title, onClick callback)
 *
 * @param title Le titre du bouton (ex: "Modifier le nom")
 * @param onClick Callback appelé lors du clic
 */
@Composable
fun ProfileActionButton(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
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
