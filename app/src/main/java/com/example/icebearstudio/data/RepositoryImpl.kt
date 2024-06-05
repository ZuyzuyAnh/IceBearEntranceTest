package com.example.icebearstudio.data

import android.util.Log
import com.example.icebearstudio.data.local.Task
import com.example.icebearstudio.data.local.TaskDAO
import com.example.icebearstudio.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val taskDAO: TaskDAO
): Repository {
    override fun getTask(id: Int): Flow<Resource<Task>> {
        return flow {
            try {
                emit(Resource.Loading())
                val task = taskDAO.getTask(id)
                emit(Resource.Success(task))
            }catch (e: Exception){
                emit(Resource.Error(e.message!!))
            }
        }
    }

    override fun getAllTask(): Flow<Resource<List<Task>>> {
        return flow {
            try {
                emit(Resource.Loading())
                val tasks = taskDAO.getAllTask()
                emit(Resource.Success(tasks))
            }catch (e: Exception){
                emit(Resource.Error(e.message!!))
            }
        }
    }

    override fun getAllTaskByDueTime(dueTime: String): Flow<Resource<List<Task>>> {
        return flow {
            try {
                emit(Resource.Loading())
                val tasks = taskDAO.getAllTaskByDueTime(dueTime)
                emit(Resource.Success(tasks))
            }catch (e: Exception){
                emit(Resource.Error(e.message!!))
            }
        }
    }

    override suspend fun insertTask(task: Task) {
        taskDAO.insertTask(task)
    }

    override suspend fun deleteTask(id: Int) {
        taskDAO.deleteTask(id)
    }

    override suspend fun deleteAllTask() {
        taskDAO.deleteAllTask()
    }

    override suspend fun updateTask(task: Task) {
        taskDAO.updateTask(task)
    }

    override suspend fun markTask(id: Int, value: Boolean) {
        taskDAO.markTask(id, value)
    }
}