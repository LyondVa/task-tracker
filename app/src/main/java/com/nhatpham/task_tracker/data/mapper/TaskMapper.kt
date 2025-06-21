package com.nhatpham.task_tracker.data.mapper

import com.nhatpham.task_tracker.domain.model.Task as DomainTask
import com.nhatpham.task_tracker.data.source.local.entity.Task as EntityTask

fun EntityTask.toDomain(): DomainTask {
    return DomainTask(
        taskId = taskId,
        title = title,
        content = content,
        isCompleted = isCompleted,
        priorityLevel = priorityLevel
    )
}

fun DomainTask.toEntity(): EntityTask {
    return EntityTask(
        taskId = taskId,
        title = title,
        content = content,
        isCompleted = isCompleted,
        priorityLevel = priorityLevel
    )
}