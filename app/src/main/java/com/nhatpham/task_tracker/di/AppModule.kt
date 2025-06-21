package com.nhatpham.task_tracker.di

import android.content.Context
import androidx.room.Room
import com.nhatpham.task_tracker.data.repository.TaskRepositoryImpl
import com.nhatpham.task_tracker.data.source.local.dao.TaskDAO
import com.nhatpham.task_tracker.data.source.local.database.TaskTrackerDatabase
import com.nhatpham.task_tracker.domain.repository.TaskRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TaskTrackerModule {
    @Singleton
    @Provides
    fun provideTaskTrackerDatabase(@ApplicationContext appContext: Context): TaskTrackerDatabase {
        return Room.databaseBuilder(
            appContext, TaskTrackerDatabase::class.java, "task_tracker_database"
        ).build()
    }

    @Singleton
    @Provides
    fun provideTaskDAO(taskTrackerDatabase: TaskTrackerDatabase) = taskTrackerDatabase.taskDAO()

    @Provides
    @Singleton
    fun provideTaskRepository(taskDAO: TaskDAO): TaskRepository {
        return TaskRepositoryImpl(taskDAO)
    }
}