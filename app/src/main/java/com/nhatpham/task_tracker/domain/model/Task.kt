package com.nhatpham.task_tracker.domain.model

data class Task(
    val taskId: String,
    val title: String = "",
    val content: String = "",
    val isCompleted: Boolean = false,
    val priorityLevel: PriorityLevel = PriorityLevel.LOW
)