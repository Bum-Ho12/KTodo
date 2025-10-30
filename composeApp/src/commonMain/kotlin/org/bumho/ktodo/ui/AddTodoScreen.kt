package org.bumho.ktodo.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import org.bumho.ktodo.presentation.TodoEffect
import org.bumho.ktodo.presentation.TodoEvent
import org.bumho.ktodo.presentation.TodoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTodoScreen(
    viewModel: TodoViewModel,
    onNavigateBack: () -> Unit
){
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<String?>(null) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect {
            effect ->
            when(effect) {
                TodoEffect.TodoAdded -> onNavigateBack()
                is TodoEffect.ShowError -> {
                    errorMessage = effect.message
                    showError = true
                }
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Todo") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        snackbarHost = {
            if(showError){
                Snackbar(
                    action = {
                        TextButton(onClick = { showError = false }){
                            Text("Dismiss")
                        }
                    }
                ){
                    Text(errorMessage)
                }
            }
        }

    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = title.isBlank() && title.isNotEmpty()
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = {Text("Description")},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                maxLines = 5
            )

            ImagePickerButton(
                currentImageUri = imageUri,
                onImageSelected = {
                    imageUri = it
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if(title.isNotBlank()){
                        viewModel.onEvent(
                            TodoEvent.AddTodo(
                                title = title.trim(),
                                description= description.trim(),
                                imagePath = imageUri
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank()
            ){
                Text("Add Todo")
            }
        }
    }
}

@Composable
expect fun ImagePickerButton(
    currentImageUri: String?,
    onImageSelected: (String?) -> Unit
)