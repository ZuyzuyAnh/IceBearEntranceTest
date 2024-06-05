package com.example.icebearstudio.ui.screen.task_detail

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icebearstudio.data.Repository
import com.example.icebearstudio.data.local.TaskDAO
import com.example.icebearstudio.ui.screen.home.TaskUIState
import com.example.icebearstudio.ui.screen.home.toTask
import com.example.icebearstudio.ui.screen.home.toTaskUIState
import com.example.icebearstudio.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskScreenViewModel @Inject constructor(
    private val repository: Repository,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val _uiState = MutableStateFlow(TaskDetailState())
    val uiState: StateFlow<TaskDetailState> = _uiState

    private val taskId: Int = savedStateHandle["task_id"]!!

    init {
        viewModelScope.launch (Dispatchers.IO) {
            getTask(taskId)
        }
    }

    fun updateUiState(taskUIState: TaskUIState){
        _uiState.value = _uiState.value.copy(
            taskUIState = taskUIState,
            isValid = validateInput(taskUIState)
        )
    }

    suspend fun updateTask(){
        if(validateInput()){
            repository.updateTask(
                _uiState.value.taskUIState.toTask()
            )
            Log.d("updated task", "${_uiState.value.taskUIState.toTask()}")
        }
    }

    private fun validateInput(taskUIState: TaskUIState = _uiState.value.taskUIState): Boolean{
        return with(taskUIState) {
            title.isNotBlank() && description.isNotBlank() && dueTime.isNotBlank()
        }
    }

    suspend fun getTask(id: Int){
        repository.getTask(id).collect{result ->
            when(result){
                is Resource.Loading -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = true
                    )
                }
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        taskUIState = result.result!!.toTaskUIState(),
                        errorMessage = null
                    )
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }
}