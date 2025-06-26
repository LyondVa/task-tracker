package com.nhatpham.task_tracker.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.nhatpham.task_tracker.R
import com.nhatpham.task_tracker.domain.model.PriorityLevel
import com.nhatpham.task_tracker.domain.model.Task
import com.nhatpham.task_tracker.presentation.TaskTrackerEvent.DeleteTask
import com.nhatpham.task_tracker.presentation.TaskTrackerEvent.UpdateTask
import com.nhatpham.task_tracker.ui.theme.taskTrackerColors
import com.nhatpham.task_tracker.util.Resource

@Composable
fun TaskTrackerScreen(
    modifier: Modifier = Modifier,
    viewModel: TaskTrackerViewModel = hiltViewModel(),
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.taskTrackerColors.gradientStart,
                        MaterialTheme.taskTrackerColors.gradientMiddle,
                        MaterialTheme.taskTrackerColors.gradientEnd
                    )
                )
            )
    ) {
        IconButton(
            onClick = onThemeToggle,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 48.dp, end = 16.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.taskTrackerColors.glassBackground)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.taskTrackerColors.glassBorder,
                    shape = CircleShape
                )
                .zIndex(1f)
        ) {
            Icon(
                painter = if (isDarkTheme) painterResource(R.drawable.light_mode_24px) else painterResource(R.drawable.dark_mode_24px),
                contentDescription = if (isDarkTheme) "Switch to Light Mode" else "Switch to Dark Mode",
                tint = MaterialTheme.taskTrackerColors.textPrimary,
                modifier = Modifier.size(20.dp)
            )
        }

        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showDialog = true },
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 0.dp
                    ),
                    containerColor = MaterialTheme.taskTrackerColors.glassBackground,
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.taskTrackerColors.glassBorder,
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add",
                        tint = MaterialTheme.taskTrackerColors.textPrimary
                    )
                }
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            when (state.allTasksResource) {
                is Resource.Success -> {
                    val tasks = (state.allTasksResource as Resource.Success<List<Task>>).data
                    val inProgressTasks = tasks.filter { !it.isCompleted }
                    val completedTasks = tasks.filter { it.isCompleted }
                    Column(
                        modifier = modifier
                            .padding(innerPadding)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .padding(top = 24.dp)
                    ) {
                        InProgressSection(
                            tasks = inProgressTasks, taskActions = taskActions
                        )
                        Spacer(modifier = Modifier.height(24.dp))
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
}

@Composable
private fun InProgressSection(
    tasks: List<Task>,
    taskActions: TaskActions,
    modifier: Modifier = Modifier
) {
    var showTasks by remember { mutableStateOf(true) }
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.taskTrackerColors.glassBackground)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.taskTrackerColors.glassBorder,
                    shape = RoundedCornerShape(20.dp)
                )
                .clickable(onClick = { showTasks = !showTasks })
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "In Progress",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.taskTrackerColors.textPrimary
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                MaterialTheme.taskTrackerColors.glassAccent,
                                CircleShape
                            )
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.taskTrackerColors.glassBorder,
                                shape = CircleShape
                            )
                            .size(28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tasks.size.toString(),
                            color = MaterialTheme.taskTrackerColors.textPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        imageVector = if (showTasks) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                        contentDescription = if (showTasks) "Hide tasks" else "Show tasks",
                        tint = MaterialTheme.taskTrackerColors.textMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        AnimatedVisibility(showTasks) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().height(280.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
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
    tasks: List<Task>,
    taskActions: TaskActions,
    modifier: Modifier = Modifier
) {
    var showTasks by remember { mutableStateOf(true) }
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.taskTrackerColors.glassBackground)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.taskTrackerColors.glassBorder,
                    shape = RoundedCornerShape(20.dp)
                )
                .clickable(onClick = { showTasks = !showTasks })
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Completed",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.taskTrackerColors.textSecondary
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                MaterialTheme.taskTrackerColors.glassAccent,
                                CircleShape
                            )
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.taskTrackerColors.glassBorder,
                                shape = CircleShape
                            )
                            .size(28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tasks.size.toString(),
                            color = MaterialTheme.taskTrackerColors.textSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        imageVector = if (showTasks) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                        contentDescription = if (showTasks) "Hide tasks" else "Show tasks",
                        tint = MaterialTheme.taskTrackerColors.textMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        AnimatedVisibility(showTasks) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().height(280.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.taskTrackerColors.dialogBackground)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.taskTrackerColors.dialogBorder,
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            Column(
                modifier = modifier.padding(28.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = "Add New Task",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.taskTrackerColors.textPrimary
                )

                OutlinedTextField(
                    value = inputState.newTaskTitle,
                    onValueChange = onTitleChange,
                    label = {
                        Text(
                            text = "Task Title",
                            color = MaterialTheme.taskTrackerColors.textMuted
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.taskTrackerColors.glassBorder,
                        unfocusedBorderColor = MaterialTheme.taskTrackerColors.glassBorder,
                        focusedTextColor = MaterialTheme.taskTrackerColors.textPrimary,
                        unfocusedTextColor = MaterialTheme.taskTrackerColors.textSecondary,
                        cursorColor = MaterialTheme.taskTrackerColors.textPrimary
                    )
                )

                OutlinedTextField(
                    value = inputState.newTaskContent,
                    onValueChange = onContentChange,
                    label = {
                        Text(
                            text = "Task Content",
                            color = MaterialTheme.taskTrackerColors.textMuted
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.taskTrackerColors.glassBorder,
                        unfocusedBorderColor = MaterialTheme.taskTrackerColors.glassBorder,
                        focusedTextColor = MaterialTheme.taskTrackerColors.textPrimary,
                        unfocusedTextColor = MaterialTheme.taskTrackerColors.textSecondary,
                        cursorColor = MaterialTheme.taskTrackerColors.textPrimary
                    )
                )

                if (errorMessage != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Red.copy(alpha = 0.2f))
                            .border(
                                width = 1.dp,
                                color = Color.Red.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(12.dp)
                            )
                    ) {
                        Text(
                            text = "Error occurred: $errorMessage",
                            color = MaterialTheme.taskTrackerColors.textPrimary,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.taskTrackerColors.glassBackground
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "Cancel",
                            color = MaterialTheme.taskTrackerColors.textPrimary,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    Button(
                        onClick = onAddClick,
                        enabled = !isAddingTask,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (MaterialTheme.taskTrackerColors.isDark)
                                MaterialTheme.taskTrackerColors.textPrimary
                            else
                                Color.White,
                            disabledContainerColor = MaterialTheme.taskTrackerColors.glassAccent
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "Add Task",
                            color = if (MaterialTheme.taskTrackerColors.isDark)
                                MaterialTheme.taskTrackerColors.gradientStart
                            else
                                Color(0xFF667eea),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
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

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.taskTrackerColors.glassBackground)
            .border(
                width = 1.dp,
                color = MaterialTheme.taskTrackerColors.glassBorder,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = if (task.isCompleted)
                        MaterialTheme.taskTrackerColors.textCompleted
                    else
                        MaterialTheme.taskTrackerColors.textPrimary,
                    textDecoration = if (task.isCompleted)
                        TextDecoration.LineThrough
                    else
                        TextDecoration.None,
                    modifier = Modifier.weight(1f)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { isExpanded.value = !isExpanded.value }
                    ) {
                        Icon(
                            imageVector = if (isExpanded.value)
                                Icons.Default.KeyboardArrowDown
                            else
                                Icons.Default.KeyboardArrowUp,
                            contentDescription = if (isExpanded.value) {
                                "Show less"
                            } else {
                                "Show more"
                            },
                            tint = MaterialTheme.taskTrackerColors.textMuted
                        )
                    }

                    RadioButton(
                        selected = task.isCompleted,
                        onClick = { onToggleComplete(task) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.taskTrackerColors.textPrimary,
                            unselectedColor = MaterialTheme.taskTrackerColors.textDisabled
                        )
                    )

                    IconButton(
                        onClick = { onDeleteClick(task.taskId) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Delete",
                            tint = MaterialTheme.taskTrackerColors.textMuted
                        )
                    }
                }
            }

            CollapsibleSection(
                isExpanded = isExpanded.value,
                taskContent = task.content,
                isCompleted = task.isCompleted
            )
        }
    }
}

@Composable
fun CollapsibleSection(
    isExpanded: Boolean,
    modifier: Modifier = Modifier,
    taskContent: String?,
    isCompleted: Boolean = false
) {
    AnimatedVisibility(
        visible = isExpanded,
        enter = slideInVertically(),
        exit = slideOutVertically()
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.taskTrackerColors.glassAccent)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.taskTrackerColors.glassBorder,
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            Text(
                text = taskContent ?: "Empty Content",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isCompleted)
                    MaterialTheme.taskTrackerColors.textCompleted
                else
                    MaterialTheme.taskTrackerColors.textMuted,
                modifier = Modifier.padding(14.dp)
            )
        }
    }
}

private data class TaskInputUiState(
    val newTaskTitle: String = "",
    val newTaskContent: String = "",
    val selectedPriority: PriorityLevel = PriorityLevel.LOW,
)

private data class TaskActions(
    val onDelete: (String) -> Unit,
    val onToggleComplete: (Task) -> Unit,
    val onEdit: (Task) -> Unit
)