package com.example.icebearstudio.ui.screen.home

import android.util.Log
import android.widget.CheckBox
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.List
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.room.Delete
import com.example.icebearstudio.AppDateFormat
import com.example.icebearstudio.data.local.Task
import com.example.icebearstudio.ui.theme.IceBearStudioTheme
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.calendar.models.CalendarStyle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Date

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeScreenViewModel,
    navigateToTaskScreen: (Int) -> Unit,
    navigateToAddTaskScreen: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToAddTaskScreen,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .padding(
                        end = WindowInsets.safeDrawing.asPaddingValues()
                            .calculateEndPadding(LocalLayoutDirection.current)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }
        }
    ) { paddingValues ->
        HomeBody(
            modifier = Modifier.padding(paddingValues),
            uiState = uiState,
            contentPadding = paddingValues,
            onDelete = {
                coroutineScope.launch(Dispatchers.IO){
                    viewModel.deleteTask(it)
                }
            },
            onMark = {
                coroutineScope.launch(Dispatchers.IO){
                    viewModel.onMark(it)
                }
            },
            deleteMarkedTasks = {
                coroutineScope.launch(Dispatchers.IO) {
                    viewModel.deleteMarkedTasks()
                }
            },
            onFilterChange = {
                coroutineScope.launch(Dispatchers.IO) {
                    viewModel.updateUiState(it)
                }
            },
            navigateToTaskDetail = navigateToTaskScreen,
            filterState = viewModel.filterState
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeBody(
    modifier: Modifier,
    uiState: HomeState,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onMark: (TaskUIState) -> Unit,
    onDelete: (Int) -> Unit,
    deleteMarkedTasks: () -> Unit,
    onFilterChange: (String) -> Unit,
    filterState: String,
    navigateToTaskDetail: (Int) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
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
            selection = CalendarSelection.Date { date ->
                onFilterChange(date.format(AppDateFormat.dateFormatter))
            }
        )
        Text(
            text = "To-do App",
            style = MaterialTheme.typography.displayLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = contentPadding.calculateTopPadding())
        )
        if(uiState.isLoading){
            LoadingIndicator()
        }else{
            OutlinedTextField(
                value = "Filter by due time: " + filterState,
                onValueChange = { onFilterChange(it)},
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
                trailingIcon = {
                    IconButton(onClick = { onFilterChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = null
                        )
                    }
                },
                modifier = modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
            if (uiState.tasks.isEmpty()) {
                Text(
                    text = "Your ToDo is empty. Add some tasks now !!",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(contentPadding)
                )
            }
            else {
                TaskList(
                    tasks = uiState.tasks,
                    modifier = Modifier.padding(contentPadding),
                    onMark = onMark,
                    onDelete = onDelete,
                    navigateToTaskDetail = navigateToTaskDetail
                )
                Button(
                    onClick = deleteMarkedTasks,
                    enabled = uiState.anyMarkedTask
                ) {
                    Text(text = "Delete marked tasks")
                }
            }
        }
    }
}

@Composable
fun TaskList(
    modifier: Modifier = Modifier,
    tasks: List<TaskUIState>,
    onMark: (TaskUIState) -> Unit,
    onDelete: (Int) -> Unit,
    navigateToTaskDetail: (Int) -> Unit
) {
    LazyColumn(
        modifier = modifier.padding(8.dp)
    ) {
        items(tasks) { task ->
            TaskItem(
                task = task,
                onMark = onMark,
                onDelete = onDelete,
                navigateToTaskDetail = navigateToTaskDetail
            )
        }
    }
}


@Composable
fun TaskItem(
    modifier: Modifier = Modifier,
    task: TaskUIState,
    onMark: (TaskUIState) -> Unit,
    onDelete: (Int) -> Unit,
    navigateToTaskDetail: (Int) -> Unit
){
    Row(
        modifier = modifier
            .padding(8.dp)
            .border(1.dp, MaterialTheme.colorScheme.onBackground, MaterialTheme.shapes.medium)
            .padding(8.dp)
            .clickable {
                       navigateToTaskDetail(task.id)
            },
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.List,
            contentDescription = null
        )
        Text(
            text = task.title,
            style = MaterialTheme.typography.titleMedium
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Created at: ${task.createdAt}",
                style = MaterialTheme.typography.labelSmall
            )
            Text(
                text = "Due time: ${task.dueTime}",
                style = MaterialTheme.typography.labelSmall
            )
        }
        Checkbox(
            checked = task.marked,
            onCheckedChange = {
                onMark(task)
            },
        )
        Box(
            modifier = Modifier
                .wrapContentSize()
                .clickable {
                    onDelete(task.id)
                }
        ) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = null
            )
        }
    }
}
@Composable
fun LoadingIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}




//@Preview(showBackground = true)
//@Composable
//fun TaskItemPreview(){
//    IceBearStudioTheme {
//        TaskItem(
//            task = TaskUIState(
//            title = "Do homework",
//            dueTime = "6/4/2024",
//            createdAt = "3/2/2024")
//        )
//    }
//}