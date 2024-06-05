package com.example.icebearstudio.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.icebearstudio.ui.screen.home.HomeScreen
import com.example.icebearstudio.ui.screen.home.HomeScreenViewModel
import com.example.icebearstudio.ui.screen.task_adding.TaskAddingViewModel
import com.example.icebearstudio.ui.screen.task_adding.TaskAddingScreen
import com.example.icebearstudio.ui.screen.task_detail.TaskScreen
import com.example.icebearstudio.ui.screen.task_detail.TaskScreenViewModel


@Composable
fun AppNavHost(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME.route
    ){
        composable(
            route = Routes.HOME.route
        ){
            val homeScreenViewModel = hiltViewModel<HomeScreenViewModel>()
            HomeScreen(
                viewModel = homeScreenViewModel,
                navigateToTaskScreen = {taskId ->
                    navController.navigate("${Routes.TASK.route}/$taskId")
                },
                navigateToAddTaskScreen = {
                    navController.navigate(Routes.TASK_ADDING.route)
                }
            )
        }
        composable(
            route = Routes.TASK.route + "/{task_id}",
            arguments = listOf(navArgument("task_id"){ type = NavType.IntType })
        ){
            val taskScreenViewModel = hiltViewModel<TaskScreenViewModel>()
            TaskScreen(
                viewModel = taskScreenViewModel,
                navigateToHomeScreen = {
                    navController.navigate(Routes.HOME.route)
                }
            )
        }
        composable(
            route = Routes.TASK_ADDING.route
        ){
            val taskAddingViewModel = hiltViewModel<TaskAddingViewModel>()
            TaskAddingScreen(
                viewModel = taskAddingViewModel,
                navigateToHomeScreen = {
                    navController.navigate(Routes.HOME.route)
                }
            )
        }
    }
}