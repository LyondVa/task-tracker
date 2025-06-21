package com.nhatpham.task_tracker.data.repository

import com.nhatpham.task_tracker.data.mapper.toDomain
import com.nhatpham.task_tracker.data.mapper.toEntity
import com.nhatpham.task_tracker.data.source.local.dao.TaskDAO
import com.nhatpham.task_tracker.domain.model.Task
import com.nhatpham.task_tracker.domain.repository.TaskRepository
import com.nhatpham.task_tracker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import java.util.UUID
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDAO: TaskDAO
) : TaskRepository {

    override suspend fun getAllTasks(): Flow<Resource<List<Task>>> =
        taskDAO.queryAllTasks()
            .map { taskEntities ->
                try {
                    Resource.success(taskEntities.map { it.toDomain() })
                } catch (e: Exception) {
                    Resource.failure(e)
                }
            }.onStart { emit(Resource.loading()) }

    override suspend fun createTask(task: Task): Flow<Resource<Task>> = flow {
        emit(Resource.success(task))
        try {
            val taskWithId = task.copy(taskId = UUID.randomUUID().toString())
            taskDAO.insertTask(taskWithId.toEntity())
            emit(Resource.success(taskWithId))
        } catch (e: Exception) {
            emit(Resource.failure(e))
        }
    }

    override suspend fun updateTask(task: Task): Flow<Resource<Task>> = flow {
        emit(Resource.success(task))
        try {
            taskDAO.updateTask(task.toEntity())
            emit(Resource.success(task))
        } catch (e: Exception) {
            emit(Resource.failure(e))
        }
    }

    override suspend fun deleteTask(taskId: String): Flow<Resource<Unit>> = flow {
        emit(Resource.success(Unit))
        try {
            taskDAO.deleteTask(taskId)
            emit(Resource.success(Unit))
        } catch (e: Exception) {
            emit(Resource.failure(e))
        }
    }
}