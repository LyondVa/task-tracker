package com.nhatpham.task_tracker.data.source.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nhatpham.task_tracker.data.source.local.dao.TaskDAO
import com.nhatpham.task_tracker.data.source.local.entity.Task

@Database(entities = [Task::class], version = 1)
abstract class TaskTrackerDatabase : RoomDatabase() {
    abstract fun taskDAO(): TaskDAO
}

