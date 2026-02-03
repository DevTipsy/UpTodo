package com.example.training.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.res.stringResource
import com.example.training.R
import com.example.training.model.Category
import com.example.training.ui.theme.TrainingTheme
import com.example.training.viewmodel.AuthViewModel
import com.example.training.viewmodel.CategoryViewModel
import com.example.training.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    authViewModel: AuthViewModel,
    viewModel: TaskViewModel = viewModel(),
    categoryViewModel: CategoryViewModel = viewModel(),
    onDismiss: () -> Unit,
    onTaskAdded: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var selectedCategoryIndex by remember { mutableStateOf(0) }
    var showDatePicker by remember { mutableStateOf(false) }

    // Collecter les StateFlows
    val currentUser by authViewModel.currentUser.collectAsState()
    val categories by categoryViewModel.categories.collectAsState()
    val isAddingTask by viewModel.isAddingTask.collectAsState()

    // Réinitialiser l'état quand on ouvre la modale
    LaunchedEffect(Unit) {
        viewModel.resetAddTaskState()
    }

    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.add_task),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.close),
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Nom de la tâche
            Text(
                text = stringResource(R.string.task_name),
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.87f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text(stringResource(R.string.task_name_placeholder), color = Color.White.copy(alpha = 0.5f)) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF8687E7),
                    unfocusedBorderColor = Color(0xFF979797),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Sélection de catégorie
            Text(
                text = stringResource(R.string.category),
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.87f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Grille des catégories (composable séparé pour améliorer les performances)
            CategoryGrid(
                categories = categories,
                selectedCategoryIndex = selectedCategoryIndex,
                onCategorySelected = { selectedCategoryIndex = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Sélection de date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Icon(
                        painter = painterResource(android.R.drawable.ic_menu_today),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                OutlinedButton(
                    onClick = { showDatePicker = true },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(dateFormatter.format(Date(selectedDate)))
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Boutons d'action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF8687E7)
                    ),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.cancel))
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = {
                        if (title.isNotBlank() && categories.isNotEmpty()) {
                            val selectedCategory = categories[selectedCategoryIndex]
                            currentUser?.let { user ->
                                viewModel.addTask(
                                    title = title,
                                    date = selectedDate,
                                    category = selectedCategory.name,
                                    userId = user.id,
                                    onSuccess = {
                                        onDismiss()
                                        onTaskAdded()
                                    }
                                )
                            }
                        }
                    },
                    enabled = title.isNotBlank() && !isAddingTask && currentUser != null,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8687E7),
                        disabledContainerColor = Color(0xFF8687E7).copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    if (isAddingTask) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(stringResource(R.string.create))
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { selectedDate = it }
                    showDatePicker = false
                }) {
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

// SOLUTION 3: Diviser les composables longs pour améliorer les performances de compilation

@Composable
private fun CategoryGrid(
    categories: List<Category>,
    selectedCategoryIndex: Int,
    onCategorySelected: (Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Première rangée (5 catégories)
        CategoryRow(
            categories = categories.take(5),
            startIndex = 0,
            selectedCategoryIndex = selectedCategoryIndex,
            onCategorySelected = onCategorySelected
        )

        // Deuxième rangée (5 catégories suivantes)
        CategoryRow(
            categories = categories.drop(5).take(5),
            startIndex = 5,
            selectedCategoryIndex = selectedCategoryIndex,
            onCategorySelected = onCategorySelected
        )
    }
}

@Composable
private fun CategoryRow(
    categories: List<Category>,
    startIndex: Int,
    selectedCategoryIndex: Int,
    onCategorySelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEachIndexed { rowIndex, category ->
            val index = rowIndex + startIndex
            val isSelected = selectedCategoryIndex == index

            CategoryItem(
                category = category,
                isSelected = isSelected,
                onClick = { onCategorySelected(index) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun CategoryItem(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .aspectRatio(1f)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) Color(0xFF8687E7) else Color(0xFF979797),
                shape = RoundedCornerShape(4.dp)
            )
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(category.getIconDrawable()),
            contentDescription = category.name,
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = category.name,
            fontSize = 10.sp,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview
@Composable
private fun TaskDetailScreenPreview() {
    TrainingTheme {
        TaskDetailScreen(
            authViewModel = AuthViewModel(),
            viewModel = TaskViewModel(),
            onDismiss = {},
            onTaskAdded = {}
        )
    }
}
