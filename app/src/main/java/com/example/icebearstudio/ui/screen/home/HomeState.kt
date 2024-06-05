package com.example.icebearstudio.ui.screen.home

import com.example.icebearstudio.AppDateFormat
import com.example.icebearstudio.data.local.Task
import java.time.LocalDate

data class HomeState(
    val tasks: List<TaskUIState> = listOf(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val eventMessage: String? = null,
    val anyMarkedTask: Boolean = false,
)

data class TaskUIState(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val createdAt: String = "",
    val dueTime: String = "",
    val marked: Boolean = false
)

fun TaskUIState.toTask() = Task(
    id = this.id,
    title = this.title,
    description = this.description,
    createdAt = LocalDate.now(),
    dueTime = LocalDate.parse(this.dueTime, AppDateFormat.dateFormatter),
    marked = this.marked
)

fun Task.toTaskUIState() = TaskUIState(
    id = this.id,
    title = this.title,
    description = this.description,
    createdAt = this.createdAt.format(AppDateFormat.dateFormatter),
    dueTime = this.dueTime.format(AppDateFormat.dateFormatter),
    marked = this.marked
)