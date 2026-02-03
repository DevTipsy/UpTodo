package com.example.training.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.training.R
import com.example.training.model.Category
import com.example.training.ui.theme.TrainingTheme
import com.example.training.util.DateUtils
import com.example.training.viewmodel.AuthViewModel
import com.example.training.viewmodel.CalendarViewModel
import com.example.training.viewmodel.CategoryViewModel
import com.example.training.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AddTaskScreen(
    authViewModel: AuthViewModel,
    viewModel: TaskViewModel = viewModel(),
    categoryViewModel: CategoryViewModel = viewModel(),
    calendarViewModel: CalendarViewModel = viewModel(),
    onAddTask: () -> Unit,
    onNavigateToProfile: () -> Unit = {}
) {
    // Collecter les StateFlows
    val currentUser by authViewModel.currentUser.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val categories by categoryViewModel.categories.collectAsState()

    // selectedDate est un MutableState, pas un StateFlow
    val selectedDate = calendarViewModel.selectedDate

    // Charger les tâches au démarrage selon l'utilisateur connecté
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            viewModel.startObservingTasks(user.id)
        }
    }

    // Cleanup quand on quitte l'écran
    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopObservingTasks()
        }
    }

    // Filtrer les tâches avec derivedStateOf pour optimiser les recompositions
    // Le filtrage ne se refait que si tasks ou selectedDate changent
    val filteredTasks = remember(tasks, selectedDate) {
        derivedStateOf {
            tasks.filter { task ->
                DateUtils.isSameDay(task.date, selectedDate.timeInMillis)
            }
        }
    }.value

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Calendrier horizontal
            CalendarScreen(
                calendarViewModel = calendarViewModel,
                modifier = Modifier.fillMaxWidth(),
                onAvatarClick = onNavigateToProfile
            )

            // Contenu selon les tâches filtrées
            if (filteredTasks.isEmpty()) {
                // Écran vide si aucune tâche pour cette date
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.checklist),
                        contentDescription = stringResource(R.string.logo_checklist),
                        modifier = Modifier.size(227.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.what_do_today),
                        color = Color.White,
                        fontSize = 20.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.tap_add_task),
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            } else {
                // Liste des tâches filtrées
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .padding(bottom = 80.dp) // Espace pour le FAB
                ) {
                    items(filteredTasks) { task ->
                        TaskItem(
                            task = task,
                            category = categories.find { it.name == task.category }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onAddTask,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = Color(0xFF8875FF)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(R.string.ajouter_tache),
                tint = Color.White
            )
        }
    }
}

@Composable
private fun TaskItem(
    task: com.example.training.model.Task,
    category: Category?
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(task.date))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1D1D1D), RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFF363636), RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icône de la catégorie
        if (category != null) {
            Image(
                painter = painterResource(category.getIconDrawable()),
                contentDescription = category.name,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        Color(category.color),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.Gray, RoundedCornerShape(8.dp))
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Informations de la tâche
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = task.title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = formattedDate,
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 14.sp
                )
                if (category != null) {
                    Text(
                        text = " • ${category.name}",
                        color = Color(category.color),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun AddTaskScreenPreview() {
    TrainingTheme {
        AddTaskScreen(
            authViewModel = AuthViewModel(),
            viewModel = TaskViewModel(),
            categoryViewModel = CategoryViewModel(),
            onAddTask = {}
        )
    }
}
