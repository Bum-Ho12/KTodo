package org.bumho.ktodo.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.bumho.ktodo.presentation.TodoViewModel

@Composable
fun App(viewModel: TodoViewModel){
    MaterialTheme {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = "todo_list"
        ){
            composable("todo_list"){
                TodoListScreen(
                    viewModel = viewModel,
                    onAddClick = {navController.navigate("add_todo")}
                )
            }
            composable("add_todo"){
                AddTodoScreen(
                    viewModel = viewModel,
                    onNavigateBack = {navController.popBackStack()}
                )
            }
        }
    }
}