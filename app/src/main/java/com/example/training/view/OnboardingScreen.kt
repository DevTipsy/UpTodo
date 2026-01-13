package com.example.training.ui  // ← minuscule !

import android.graphics.DashPathEffect
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.training.ui.theme.TrainingTheme
import com.example.training.viewmodel.OnboardingViewModel

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val pageData = viewModel.currentPageData

    Box(modifier = modifier.fillMaxSize()) {
        // Bouton SKIP
        Text(
            text = "SKIP",
            color = Color.Gray,
            fontSize = 16.sp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .clickable { viewModel.skipToEnd() }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Contenu
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(pageData.imageRes),
                    contentDescription = pageData.title,
                    modifier = Modifier.size(250.dp)
                )

                Spacer(modifier = Modifier.height(40.dp))

                PageIndicators(
                    pageCount = viewModel.pageCount,
                    currentPage = viewModel.currentPage
                )

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = pageData.title,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = pageData.description,
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
            }

            // Bottom
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Button(
                    onClick = { viewModel.nextPage() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8875FF)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = if (viewModel.isLastPage) "GET STARTED" else "NEXT",
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun PageIndicators(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(pageCount) { index ->
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .then(
                        if (index == currentPage) {
                            Modifier.background(Color.White, CircleShape)
                        } else {
                            Modifier.border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                        }
                    )
            )
        }
    }
}

@Preview
@Composable
fun OnboardingScreenPreview() {
    TrainingTheme {
        OnboardingScreen()
    }
}