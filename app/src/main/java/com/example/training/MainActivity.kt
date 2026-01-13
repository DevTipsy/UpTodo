package com.example.training

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.training.ui.theme.TrainingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TrainingTheme {
                    IntroScreen()
                }
            }
        }
    }

@Composable
fun IntroScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize() // Occupe tout l'écran
            .background(Color.Black), // Fond noir

        verticalArrangement = Arrangement.Center, // Centre le contenu en vertical
        horizontalAlignment = Alignment.CenterHorizontally // Centre le contenu en horizontal
    ) {
        // Image locale
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "Logo UpTodo",
                    modifier = Modifier
                    .size(100.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Texte
        Text(
            text = "UpTodo",
            fontSize = 24.sp,
            color = Color.White
        )
    }
}

@Preview
@Composable
fun IntroScreenPreview() {
    TrainingTheme {
        IntroScreen()
    }
}