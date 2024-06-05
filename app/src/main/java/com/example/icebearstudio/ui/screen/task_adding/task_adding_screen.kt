package com.example.icebearstudio.ui.screen.task_adding

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.icebearstudio.AppDateFormat
import com.example.icebearstudio.ui.screen.home.TaskUIState
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarStyle
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskAddingScreen(
    modifier: Modifier = Modifier,
    viewModel: TaskAddingViewModel,
    navigateToHomeScreen: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(
                    text = "Add task",
                    textAlign = TextAlign.Center
                ) },
                navigationIcon = {
                    IconButton(onClick = navigateToHomeScreen) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { it ->
        TaskAddingBody(
            taskUiState = viewModel.uiState.task,
            onValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.saveTask()
                    navigateToHomeScreen()
                }
            },
            modifier = Modifier
                .padding(
                    start = it.calculateStartPadding(LocalLayoutDirection.current),
                    top = it.calculateTopPadding(),
                    end = it.calculateEndPadding(LocalLayoutDirection.current),
                )
                .verticalScroll(rememberScrollState())
                .fillMaxWidth(),
            enabled = viewModel.uiState.isValid
        )
    }
}

@Composable
fun TaskAddingBody(
    modifier: Modifier = Modifier,
    taskUiState: TaskUIState,
    onValueChange: (TaskUIState) -> Unit,
    onSaveClick: () -> Unit,
    enabled: Boolean = true
) {
    Column(
        modifier = modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TaskInput(
            taskUiState = taskUiState,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled
        )
        Button(
            onClick = onSaveClick,
            enabled = enabled,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Submit")   
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskInput(
    modifier: Modifier = Modifier,
    taskUiState: TaskUIState,
    onValueChange: (TaskUIState) -> Unit,
    enabled: Boolean
) {
    val calendarState = rememberUseCaseState()
    val boundary = LocalDate.now()..LocalDate.now().plusYears(1)
    CalendarDialog(
        state = calendarState,
        config = CalendarConfig(
            monthSelection = true,
            yearSelection = true,
            style = CalendarStyle.WEEK,
            boundary = boundary
        ),
        selection = CalendarSelection.Date {date ->
            Log.d("Date picker selected", "$date")
            onValueChange(taskUiState.copy(dueTime = date.format(AppDateFormat.dateFormatter)))
        }
    )
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = taskUiState.title,
            onValueChange = {onValueChange(taskUiState.copy(title = it))},
            label = { Text(text = "Title") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = taskUiState.description,
            onValueChange = {onValueChange(taskUiState.copy(description = it))},
            label = { Text(text = "Description") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = taskUiState.dueTime,
            onValueChange = { onValueChange(taskUiState.copy(dueTime = it))},
            leadingIcon = {
                IconButton(onClick = { calendarState.show() }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null
                    )
                }
            },
            readOnly = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = modifier.fillMaxWidth()
        )
        if(!enabled) {
            Text(
                text = "*Please enter all fields",
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

