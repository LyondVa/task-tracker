package com.nhatpham.task_tracker.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.nhatpham.task_tracker.domain.model.PriorityLevel
import com.nhatpham.task_tracker.domain.model.Task
import com.nhatpham.task_tracker.presentation.TaskTrackerEvent.DeleteTask
import com.nhatpham.task_tracker.presentation.TaskTrackerEvent.UpdateTask
import com.nhatpham.task_tracker.util.Resource

@Composable
fun TaskTrackerScreen(
    modifier: Modifier = Modifier,
    viewModel: TaskTrackerViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    var showDialog by remember { mutableStateOf(false) }

    var inputUiState by remember { mutableStateOf(TaskInputUiState()) }

    val taskActions = TaskActions(onDelete = { viewModel.onEvent(DeleteTask(it)) },
        onToggleComplete = { viewModel.onEvent(UpdateTask(it.copy(isCompleted = !it.isCompleted))) },
        onEdit = { viewModel.onEvent(UpdateTask(it)) })


    LaunchedEffect(Unit) {
        viewModel.onEvent(TaskTrackerEvent.LoadAllTasks)
    }

    LaunchedEffect(state.createTaskResource) {
        if (state.createTaskResource is Resource.Success) {
            showDialog = false
        }
    }

    Scaffold(floatingActionButton = {
        FloatingActionButton(onClick = { showDialog = true }) {
            Icon(Icons.Default.Add, contentDescription = "Add")
        }
    }) { innerPadding ->
        when (state.allTasksResource) {
            is Resource.Success -> {
                val tasks = (state.allTasksResource as Resource.Success<List<Task>>).data
                val inProgressTasks = tasks.filter { !it.isCompleted }
                val completedTasks = tasks.filter { it.isCompleted }
                Column(
                    modifier = modifier.padding(innerPadding)
                ) {
                    InProgressSection(
                        tasks = inProgressTasks, taskActions = taskActions
                    )
                    CompletedSection(
                        tasks = completedTasks, taskActions = taskActions
                    )
                    if (showDialog) {
                        TaskInputDialogue(
                            inputState = inputUiState,
                            onTitleChange = { inputUiState = inputUiState.copy(newTaskTitle = it) },
                            onContentChange = {
                                inputUiState = inputUiState.copy(newTaskContent = it)
                            },
                            onAddClick = {
                                viewModel.onEvent(
                                    TaskTrackerEvent.CreateTask(
                                        Task(
                                            taskId = "",
                                            title = inputUiState.newTaskTitle,
                                            content = inputUiState.newTaskContent,
                                            priorityLevel = inputUiState.selectedPriority
                                        )
                                    )
                                )
                            },
                            onDismiss = {
                                showDialog = false
                            },
                            isAddingTask = state.createTaskResource is Resource.Loading,
                            errorMessage = (state.createTaskResource as? Resource.Error)?.message,
                            modifier = modifier
                        )
                    }
                }
            }

            is Resource.Error -> {

            }

            Resource.Idle -> {

            }

            is Resource.Loading -> {

            }
        }
    }
}

@Composable
private fun InProgressSection(
    tasks: List<Task>, taskActions: TaskActions, modifier: Modifier = Modifier
) {
    var showTasks by remember { mutableStateOf(true) }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = { showTasks = !showTasks })
    ) {
        Text(text = "In Progress")
        AnimatedVisibility(showTasks) {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(tasks.size) { index ->
                    TaskItem(
                        task = tasks[index],
                        onDeleteClick = taskActions.onDelete,
                        onToggleComplete = taskActions.onToggleComplete
                    )
                }
            }
        }
    }
}

@Composable
private fun CompletedSection(
    tasks: List<Task>, taskActions: TaskActions, modifier: Modifier = Modifier
) {
    var showTasks by remember { mutableStateOf(true) }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = { showTasks = !showTasks })
    ) {
        Text(text = "Completed")
        AnimatedVisibility(showTasks) {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(tasks.size) { index ->
                    TaskItem(
                        task = tasks[index],
                        onDeleteClick = taskActions.onDelete,
                        onToggleComplete = taskActions.onToggleComplete
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskInputDialogue(
    inputState: TaskInputUiState,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onAddClick: () -> Unit,
    onDismiss: () -> Unit,
    isAddingTask: Boolean,
    errorMessage: String?,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface {
            Column(modifier = modifier) {
                OutlinedTextField(value = inputState.newTaskTitle,
                    onValueChange = onTitleChange,
                    label = { Text(text = "Task Title") })
                OutlinedTextField(value = inputState.newTaskContent,
                    onValueChange = onContentChange,
                    label = { Text(text = "Task Content") })
                if (errorMessage != null) {
                    Text(
                        text = "Error occurred: $errorMessage",
                        color = MaterialTheme.colorScheme.error
                    )
                }
                Button(
                    onClick = onAddClick,
                    enabled = !isAddingTask,

                    ) {
                    Text(text = "Add Task")
                }
            }
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onDeleteClick: (String) -> Unit,
    onToggleComplete: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    val isExpanded = remember { mutableStateOf(false) }
    Column(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = task.title)
            Row(modifier = Modifier) {
                IconButton(onClick = { isExpanded.value = !isExpanded.value }) {
                    Icon(
                        imageVector = if (isExpanded.value) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                        contentDescription = if (isExpanded.value) {
                            "Show less"
                        } else {
                            "Show more"
                        }
                    )
                }
                RadioButton(selected = task.isCompleted, onClick = { onToggleComplete(task) })
                IconButton(onClick = { onDeleteClick(task.taskId) }) {
                    Icon(
                        imageVector = Icons.Default.Clear, contentDescription = "Clear"
                    )
                }
            }
        }
        CollapsibleSection(
            isExpanded = isExpanded.value,
            taskContent = task.content,
        )
    }
}

@Composable
fun CollapsibleSection(
    isExpanded: Boolean,
    modifier: Modifier = Modifier,
    taskContent: String?,
) {
    AnimatedVisibility(isExpanded) {
        Row(modifier = modifier) {
            Text(text = taskContent ?: "Empty Content")
        }
    }
}

private data class TaskInputUiState(
    val newTaskTitle: String = "",
    val newTaskContent: String = "",
    val selectedPriority: PriorityLevel = PriorityLevel.LOW,
)

private data class TaskActions(
    val onDelete: (String) -> Unit, val onToggleComplete: (Task) -> Unit, val onEdit: (Task) -> Unit
)