package com.example.training.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.training.R
import com.example.training.data.mapper.TaskMapper.toDomain
import com.example.training.data.mapper.TaskMapper.toDto
import com.example.training.model.Task
import com.example.training.repository.TaskRepository
import com.example.training.util.Result
import com.example.training.util.UiEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TaskViewModel : ViewModel() {

    private val taskRepository = TaskRepository()

    // État : Liste des tâches (Domain Models)
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    // État : Chargement pour l'ajout de tâche (séparé du chargement général)
    private val _isAddingTask = MutableStateFlow(false)
    val isAddingTask: StateFlow<Boolean> = _isAddingTask.asStateFlow()

    // Événements UI
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    // Job pour gérer l'observation des tâches
    private var observeTasksJob: Job? = null

    /**
     * Démarrer l'observation des tâches en temps réel
     * Appelé depuis l'UI dans un LaunchedEffect(currentUser)
     */
    fun startObservingTasks(userId: String) {
        observeTasksJob?.cancel()

        observeTasksJob = viewModelScope.launch {
            taskRepository.observeTasks(userId)
                .map { taskDtos ->
                    taskDtos.map { it.toDomain() }.sortedBy { it.date }
                }
                .catch { e ->
                    _uiEvent.send(UiEvent.ShowSnackbar(R.string.error_generic))
                }
                .collect { taskList ->
                    _tasks.value = taskList
                }
        }
    }

    /**
     * Arrêter l'observation des tâches
     * Appelé depuis l'UI dans un DisposableEffect.onDispose
     */
    fun stopObservingTasks() {
        observeTasksJob?.cancel()
        observeTasksJob = null
    }

    /**
     * Réinitialiser l'état d'ajout de tâche
     * Appelé quand on ouvre la modale TaskDetailScreen
     */
    fun resetAddTaskState() {
        _isAddingTask.value = false
    }

    /**
     * Ajouter une nouvelle tâche
     * Garde le callback onSuccess pour fermer la modale d'ajout
     */
    fun addTask(
        title: String,
        date: Long,
        category: String,
        userId: String,
        onSuccess: () -> Unit
    ) {
        if (title.isBlank()) {
            viewModelScope.launch {
                _uiEvent.send(UiEvent.ShowSnackbar(R.string.validation_title_required))
            }
            return
        }

        viewModelScope.launch {
            try {
                _isAddingTask.value = true

                val task = Task(
                    id = "",
                    title = title,
                    date = date,
                    category = category,
                    userId = userId,
                    isCompleted = false
                )

                val taskDto = task.toDto()

                when (val result = taskRepository.addTask(taskDto)) {
                    is Result.Success -> {
                        onSuccess()
                        _uiEvent.send(UiEvent.ShowSnackbar(R.string.success_task_added))
                    }
                    is Result.Error -> {
                        val messageRes = result.messageRes ?: R.string.error_generic
                        _uiEvent.send(UiEvent.ShowSnackbar(messageRes))
                    }
                    Result.Loading -> {}
                }
            } catch (e: Exception) {
                _uiEvent.send(UiEvent.ShowSnackbar(R.string.error_generic))
            } finally {
                _isAddingTask.value = false
            }
        }
    }

    /**
     * Marquer une tâche comme complétée/incomplétée
     * (Optionnel - pour le futur)
     */
    fun toggleTaskCompletion(taskId: String, isCompleted: Boolean) {
        viewModelScope.launch {
            val updates = mapOf("isCompleted" to isCompleted)
            when (val result = taskRepository.updateTask(taskId, updates)) {
                is Result.Error -> {
                    _uiEvent.send(UiEvent.ShowSnackbar(R.string.error_task_update))
                }
                else -> {}  // Succès silencieux (la UI se met à jour via le Flow)
            }
        }
    }

    /**
     * Supprimer une tâche
     * (Optionnel - pour le futur)
     */
    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            when (val result = taskRepository.deleteTask(taskId)) {
                is Result.Success -> {
                    _uiEvent.send(UiEvent.ShowSnackbar(R.string.success_task_deleted))
                }
                is Result.Error -> {
                    _uiEvent.send(UiEvent.ShowSnackbar(R.string.error_task_delete))
                }
                Result.Loading -> {}
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Le viewModelScope annule automatiquement observeTasksJob
        // Mais on peut le faire explicitement pour plus de clarté
        stopObservingTasks()
    }
}
