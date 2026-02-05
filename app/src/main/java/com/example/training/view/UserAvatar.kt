package com.example.training.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.training.R
import com.example.training.ui.theme.TrainingTheme

@Composable
fun Avatar (
    modifier : Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    // Image de l'avatar du user connecté
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Image(
            painter = painterResource(R.drawable.avatar),
            contentDescription = stringResource(R.string.avatar),
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .clickable(onClick = onClick)
        )
    }
}

@Preview
@Composable
private fun AvatarPreview()
{
    TrainingTheme {
        Avatar()
    }
}