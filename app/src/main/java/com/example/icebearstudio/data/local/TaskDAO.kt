package com.example.icebearstudio.data.local

import android.content.ClipData.Item
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.icebearstudio.data.local.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Query(
        "DELETE FROM task WHERE id = :id"
    )
    suspend fun deleteTask(id: Int)

    @Update
    suspend fun updateTask(task: Task)

    @Query(
        "DELETE FROM task WHERE marked = true"
    )
    suspend fun deleteAllTask()

    @Query(
        "UPDATE task SET marked = :value WHERE id = :id"
    )
    suspend fun markTask(id: Int, value: Boolean)

    @Query(
        "SELECT * FROM task WHERE id = :id"
    )
    fun getTask(id: Int): Task

    @Query(
        "SELECT * FROM task ORDER BY date(createdAt)"
    )
    fun getAllTask(): List<Task>

    @Query(
        "SELECT * FROM task WHERE dueTime = :dueTime ORDER BY date(createdAt)"
    )
    fun getAllTaskByDueTime(dueTime: String): List<Task>
}