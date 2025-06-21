package com.nhatpham.task_tracker.domain.repository

import com.nhatpham.task_tracker.domain.model.Task
import com.nhatpham.task_tracker.util.Resource
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    suspend fun getAllTasks(): Flow<Resource<List<Task>>>
    suspend fun createTask(task: Task): Flow<Resource<Task>>
    suspend fun updateTask(task: Task): Flow<Resource<Task>>
    suspend fun deleteTask(taskId: String): Flow<Resource<Unit>>
}