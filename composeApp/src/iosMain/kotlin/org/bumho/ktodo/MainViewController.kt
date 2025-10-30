package org.bumho.ktodo

import androidx.compose.ui.window.ComposeUIViewController
import org.bumho.ktodo.data.DatabaseBuilder
import org.bumho.ktodo.domain.TodoRepository
import org.bumho.ktodo.presentation.TodoViewModel
import org.bumho.ktodo.ui.App

fun MainViewController() = ComposeUIViewController {
    val database = DatabaseBuilder().build()
    val repository = TodoRepository(database.todoDao())
    val viewModel = TodoViewModel(repository)
    App(viewModel)
}