package com.nhatpham.task_tracker.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nhatpham.task_tracker.domain.model.PriorityLevel

@Entity
data class Task(
    @PrimaryKey val taskId: String,
    val title: String,
    val content: String,
    val isCompleted: Boolean,
    val priorityLevel: PriorityLevel,
)
