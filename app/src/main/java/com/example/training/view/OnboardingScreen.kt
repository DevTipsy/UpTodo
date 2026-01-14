package com.example.training.view

import android.graphics.DashPathEffect
import androidx.compose.foundation.Image
import androidx.compose.material3.TextButton
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
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
    onComplete: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val pageData = viewModel.currentPageData

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Contenu principal
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(pageData.imageRes),
                    contentDescription = pageData.title,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .aspectRatio(1f)
                )

                Spacer(modifier = Modifier.height(50.dp))

                // Indicateurs de page
                PageIndicators(
                    pageCount = viewModel.pageCount,
                    currentPage = viewModel.currentPage
                )

                Spacer(modifier = Modifier.height(50.dp))

                Text(
                    text = pageData.title,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(42.dp))

                Text(
                    text = pageData.description,
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.87f),
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            // Bouton PREVIOUS
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(50.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Bouton BACK (visible sauf sur la première page)
                    if (viewModel.currentPage > 0) {
                        TextButton(
                            onClick = { viewModel.previousPage() },
                            modifier = Modifier.padding(start = 0.dp)
                        ) {
                            Text(
                                text = "BACK",
                                color = Color.White.copy(alpha = 0.44f),
                                fontSize = 16.sp
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    // Bouton NEXT/GET STARTED
                    Button(
                        onClick = {
                            if (viewModel.isLastPage) {
                                onComplete()
                            } else {
                                viewModel.nextPage()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF8875FF)
                        ),
                        modifier = Modifier
                            .width(if (viewModel.isLastPage) 155.dp else 100.dp)
                            .height(48.dp),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = if (viewModel.isLastPage) "GET STARTED" else "NEXT",
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Bouton SKIP (placé après la Column pour être au-dessus)
        Text(
            text = "SKIP",
            color = Color.White.copy(alpha = 0.44f),
            fontSize = 16.sp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 14.dp, start = 24.dp)
                .clickable { onComplete() }
        )
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
                    .width(26.dp)
                    .height(4.dp)
                    .background(
                        if (index == currentPage) Color.White else Color.White.copy(alpha = 0.7f),
                        androidx.compose.foundation.shape.RoundedCornerShape(56.dp)
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