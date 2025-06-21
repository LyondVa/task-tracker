package com.nhatpham.task_tracker.domain.usecase

import com.nhatpham.task_tracker.domain.model.Task
import com.nhatpham.task_tracker.domain.repository.TaskRepository
import com.nhatpham.task_tracker.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeleteTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(taskId: String): Flow<Resource<Unit>> =
        taskRepository.deleteTask(taskId)
}