package com.example.icebearstudio.ui.screen.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icebearstudio.data.Repository
import com.example.icebearstudio.data.local.Task
import com.example.icebearstudio.data.local.TaskDAO
import com.example.icebearstudio.ui.screen.task_adding.TaskAddingState
import com.example.icebearstudio.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val repository: Repository
): ViewModel() {
    private val _uiState = MutableStateFlow(HomeState())
    val uiState = _uiState.asStateFlow()

    var filterState by mutableStateOf("")
        private set

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getAllTask()
        }
    }

    suspend fun updateUiState(value: String){
        filterState = value
        if(value.isBlank()){
            getAllTask()
        }else{
            getAllTaskByDueTime(value)
        }
    }

    suspend fun deleteTask(id: Int) {
        repository.deleteTask(id)
        getAllTask()
    }

    suspend fun onMark(taskUiState: TaskUIState){
        repository.markTask(taskUiState.id, !taskUiState.marked)
        getAllTask()
    }

    suspend fun deleteMarkedTasks(){
        repository.deleteAllTask()
        getAllTask()
    }

    suspend fun getAllTask() {
        repository.getAllTask().collect{result ->
            when(result){
                is Resource.Loading -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = true,
                    )
                    Log.d("loading state", "${_uiState.value.isLoading}")
                }
                is Resource.Success -> {
                    val taskList = result.result!!.map { it.toTaskUIState() }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        tasks = taskList,
                        anyMarkedTask = taskList.any { it.marked }
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

    suspend fun getAllTaskByDueTime(dueTime: String){
        repository.getAllTaskByDueTime(dueTime).collect{result ->
            when(result){
                is Resource.Loading -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = true,
                    )
                }
                is Resource.Success -> {
                    val taskList = result.result!!.map { it.toTaskUIState() }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        tasks = taskList,
                        anyMarkedTask = taskList.any{ it.marked }
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