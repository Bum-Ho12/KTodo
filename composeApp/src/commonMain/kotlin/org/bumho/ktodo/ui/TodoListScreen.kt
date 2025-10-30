package org.bumho.ktodo.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import org.bumho.ktodo.domain.Todo
import org.bumho.ktodo.presentation.TodoEffect
import org.bumho.ktodo.presentation.TodoEvent
import org.bumho.ktodo.presentation.TodoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    viewModel: TodoViewModel,
    onAddClick: ()-> Unit
){
    val state by viewModel.state.collectAsState()
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember {mutableStateOf("")}

    LaunchedEffect(viewModel){
        viewModel.effect.collect {
            effect -> when(effect){
                is TodoEffect.ShowError -> {
                    snackbarMessage = effect.message
                    showSnackbar = true
                }
                TodoEffect.TodoDeleted -> {
                    snackbarMessage = "Todo deleted"
                    showSnackbar = true
                }
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Todos") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick){
                Icon(Icons.Default.Add, contentDescription = "Add Todo")
            }
        },
        snackbarHost = {
            if(showSnackbar){
                Snackbar(
                    action = {
                        TextButton(onClick = {showSnackbar = false}){
                            Text("Dismiss")
                        }
                    }
                ){
                    Text(snackbarMessage)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(padding)
        ) {
            if (state.isLoading && state.todos.isEmpty()){
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        "No todos yet. Add one!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }else{
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.todos, key = { it.id }) {
                        todo ->
                        TodoItem(
                            todo = todo,
                            onToggle = { viewModel.onEvent(TodoEvent.ToggleTodoComplete(todo)) },
                            onDelete = {viewModel.onEvent(TodoEvent.DeleteTodo(todo))},
                            onClick = { viewModel.onEvent(TodoEvent.SelectTodo(todo)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TodoItem(
    todo: Todo,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit,
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick= onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Checkbox(
                checked = todo.isCompleted,
                onCheckedChange = {onToggle()}
            )
            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ){
                Text(
                    text = todo.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if(todo.isCompleted){
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else{
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                if(todo.description.isNotEmpty()){
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = todo.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }
            }

            TodoImageThumbnail(imagePath = todo.imagePath)

            IconButton(onClick= onDelete){
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
expect fun TodoImageThumbnail(imagePath: String?)