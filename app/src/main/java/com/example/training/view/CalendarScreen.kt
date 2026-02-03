package com.example.training.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.training.R
import com.example.training.ui.theme.TrainingTheme
import com.example.training.viewmodel.CalendarViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CalendarScreen(
    calendarViewModel: CalendarViewModel = viewModel(),
    modifier: Modifier = Modifier,
    onAvatarClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(top = 32.dp, bottom = 16.dp)
    ) {
        // Avatar en haut
        Avatar(
            modifier = Modifier.padding(horizontal = 16.dp),
            onClick = onAvatarClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Header avec date et chevrons
        CalendarHeader(
            formattedDate = calendarViewModel.getFormattedHeaderDate(),
            onPreviousDay = { calendarViewModel.goToPreviousDay() },
            onNextDay = { calendarViewModel.goToNextDay() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Scroll horizontal des 7 jours
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(
                items = calendarViewModel.weekDays,
                key = { day -> day.timeInMillis }  // ← KEY ajouté pour optimisation
            ) { day ->
                DayCard(
                    day = day,
                    isSelected = calendarViewModel.isSameDay(day, calendarViewModel.selectedDate),
                    onClick = { calendarViewModel.selectDate(day) }
                )
            }
        }
    }
}

@Composable
private fun CalendarHeader(
    formattedDate: String,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousDay) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = stringResource(R.string.jour_precedent),
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }

        Text(
            text = formattedDate,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )

        IconButton(onClick = onNextDay) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = stringResource(R.string.jour_suivant),
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
private fun DayCard(
    day: Calendar,
    isSelected: Boolean,
    onClick: () -> Unit,
    taskCount: Int = 0
) {
    Card(
        modifier = Modifier
            .width(60.dp)
            .height(80.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF8875FF) else Color(0xFF1D1D1D)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Nom du jour (ex: "Mer")
            Text(
                text = SimpleDateFormat("EEE", Locale.FRENCH).format(day.time).replaceFirstChar { it.uppercase() },
                color = Color.White.copy(alpha = if (isSelected) 1f else 0.6f),
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Numéro du jour (ex: "22")
            Text(
                text = day.get(Calendar.DAY_OF_MONTH).toString(),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            // Badge nombre de tâches (optionnel, pour plus tard)
            if (taskCount > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(Color.Red, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = taskCount.toString(),
                        color = Color.White,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun CalendarPreview() {
    TrainingTheme {
        CalendarScreen()
    }
}
