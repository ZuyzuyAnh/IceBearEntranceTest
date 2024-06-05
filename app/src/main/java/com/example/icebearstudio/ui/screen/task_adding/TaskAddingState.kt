package com.example.icebearstudio.ui.screen.task_adding

import com.example.icebearstudio.ui.screen.home.TaskUIState

data class TaskAddingState(
    val task: TaskUIState = TaskUIState(),
    val isValid: Boolean = false
)
