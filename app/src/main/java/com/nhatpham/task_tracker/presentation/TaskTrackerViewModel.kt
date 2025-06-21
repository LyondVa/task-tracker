package com.nhatpham.task_tracker.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhatpham.task_tracker.domain.model.Task
import com.nhatpham.task_tracker.domain.usecase.CreateTaskUseCase
import com.nhatpham.task_tracker.domain.usecase.DeleteTaskUseCase
import com.nhatpham.task_tracker.domain.usecase.GetAllTaskUseCase
import com.nhatpham.task_tracker.domain.usecase.UpdateTaskUseCase
import com.nhatpham.task_tracker.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TaskTrackerViewModel @Inject constructor(
    private val createTaskUseCase: CreateTaskUseCase,
    private val getAllTaskUseCase: GetAllTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(TaskTrackerState())
    val state: StateFlow<TaskTrackerState> = _state.asStateFlow()

    private fun loadAllTasks() {
        viewModelScope.launch {
            getAllTaskUseCase().collect { resource ->
                _state.value = _state.value.copy(allTasksResource = resource)
            }
        }
    }

    private fun createTask(task: Task) {
        Timber.d("createTask: $task")
        viewModelScope.launch {
            createTaskUseCase(task).collect { resource ->
                _state.value = _state.value.copy(createTaskResource = resource)
            }
        }
    }

    private fun updateTask(task: Task) {
        viewModelScope.launch {
            updateTaskUseCase(task).collect { resource ->
                _state.value = _state.value.copy(updateTaskResource = resource)
            }
        }
    }

    private fun deleteTask(taskId: String) {
        viewModelScope.launch {
            deleteTaskUseCase(taskId).collect { resource ->
                _state.value = _state.value.copy(deleteTaskResource = resource)
            }
        }
    }

    fun onEvent(event: TaskTrackerEvent) {
        when (event) {
            is TaskTrackerEvent.LoadAllTasks -> {
                loadAllTasks()
            }

            is TaskTrackerEvent.CreateTask -> {
                createTask(event.task)
            }

            is TaskTrackerEvent.UpdateTask -> {
                updateTask(event.task)
            }

            is TaskTrackerEvent.DeleteTask -> {
                deleteTask(event.taskId)
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(
            allTasksResource = Resource.Idle,
            createTaskResource = null,
            updateTaskResource = null,
            deleteTaskResource = null
        )
    }

    fun clearState() {
        //TODO
    }
}

data class TaskTrackerState(
    val allTasksResource: Resource<List<Task>> = Resource.Idle,
    val createTaskResource: Resource<Task>? = null,
    val updateTaskResource: Resource<Task>? = null,
    val deleteTaskResource: Resource<Unit>? = null,
)

sealed class TaskTrackerEvent {
    object LoadAllTasks : TaskTrackerEvent()
    data class CreateTask(val task: Task) : TaskTrackerEvent()
    data class UpdateTask(val task: Task) : TaskTrackerEvent()
    data class DeleteTask(val taskId: String) : TaskTrackerEvent()
}