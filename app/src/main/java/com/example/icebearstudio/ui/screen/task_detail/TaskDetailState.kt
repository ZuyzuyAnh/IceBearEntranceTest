package com.example.icebearstudio.ui.screen.task_detail

import com.example.icebearstudio.ui.screen.home.TaskUIState

data class TaskDetailState(
    val taskUIState: TaskUIState = TaskUIState(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val isValid: Boolean = true
)
