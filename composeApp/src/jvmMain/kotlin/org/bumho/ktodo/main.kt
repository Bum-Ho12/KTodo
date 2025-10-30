package org.bumho.ktodo

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.bumho.ktodo.data.DatabaseBuilder
import org.bumho.ktodo.domain.TodoRepository
import org.bumho.ktodo.presentation.TodoViewModel
import org.bumho.ktodo.ui.App

fun main() = application {
    val database = DatabaseBuilder().builder()
    val repository = TodoRepository(database.todoDao())
    val viewModel = TodoViewModel(repository)

    Window(
        onCloseRequest = {
            viewModel.onCleared()
            exitApplication() },
        title = "KTodo",
        state = rememberWindowState(width=androidx.compose.ui.unit.dp(400), height = androidx.compose.ui.unit.dp(700))
    ) {
        App(viewModel)
    }
}