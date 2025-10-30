package org.bumho.ktodo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.bumho.ktodo.data.DatabaseBuilder
import org.bumho.ktodo.domain.TodoRepository
import org.bumho.ktodo.presentation.TodoViewModel
import org.bumho.ktodo.ui.App

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: TodoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val database = DatabaseBuilder(applicationContext).build()
        val repository = TodoRepository(database.todoDao())
        viewModel = TodoViewModel(repository)

        setContent {
            App(viewModel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onCleared()
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}