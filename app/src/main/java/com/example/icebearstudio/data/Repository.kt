package com.example.icebearstudio.data

import com.example.icebearstudio.data.local.Task
import com.example.icebearstudio.util.Resource
import kotlinx.coroutines.flow.Flow

interface Repository {
    fun getTask(id: Int): Flow<Resource<Task>>

    fun getAllTask(): Flow<Resource<List<Task>>>

    fun getAllTaskByDueTime(dueTime: String): Flow<Resource<List<Task>>>

    suspend fun insertTask(task: Task)

    suspend fun deleteTask(id: Int)

    suspend fun deleteAllTask()

    suspend fun updateTask(task: Task)

    suspend fun markTask(id: Int, value: Boolean)
}