package com.example.icebearstudio.ui.screen.task_adding

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.icebearstudio.data.Repository
import com.example.icebearstudio.ui.screen.home.TaskUIState
import com.example.icebearstudio.ui.screen.home.toTask
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TaskAddingViewModel @Inject constructor(
    private val repository: Repository
): ViewModel() {
    var uiState by mutableStateOf(TaskAddingState())
        private set

    fun updateUiState(taskUIState: TaskUIState){
        uiState = TaskAddingState(task = taskUIState, isValid = validateInput(taskUIState))
    }

    suspend fun saveTask(){
        if(validateInput()){
            Log.d("save Task", "${uiState.task}/n${uiState.isValid}")
            repository.insertTask(uiState.task.toTask())
        }
    }

    private fun validateInput(taskUiState: TaskUIState = uiState.task): Boolean {
        return with(taskUiState){
            title.isNotBlank() && dueTime.isNotBlank() && description.isNotBlank()
        }
    }
}