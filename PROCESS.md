# Kotlin Multiplatform Todo App with Compose - Single Module Guide

## Prerequisites
- IntelliJ IDEA or Android Studio Narwhal 2025
- JDK 17 or higher
- Xcode (for iOS build - requires macOS)
- Windows 11/10 or macOS

## Project Overview
Building a cross-platform Todo app with:
- ✅ Kotlin Multiplatform (Android, iOS, Desktop)
- ✅ Jetpack Compose & Compose Multiplatform
- ✅ Room Database (with KMP support)
- ✅ MVI Architecture
- ✅ Image support
- ✅ Local database storage

**Project Structure**: Single-module architecture (all code in `composeApp`)

---

## Step 1: Create Base Project with IntelliJ IDEA

1. Open IntelliJ IDEA
2. Select **New Project**
3. Choose **Kotlin Multiplatform**
4. Select **Compose Multiplatform Application**
5. Configure:
    - Name: `KTodo` (or your preferred name)
    - Location: Choose your directory
    - Build System: **Gradle Kotlin DSL**
6. Select platforms:
    - ✅ Android
    - ✅ iOS
    - ✅ Desktop
7. Package: `org.bumho.ktodo` (or your preferred package)
8. Click **Create**

---

## Step 2: Update Project Configuration

### 2.1 Update `gradle/libs.versions.toml`

Add these sections to your existing `libs.versions.toml`:

```toml
[versions]
# ... keep your existing versions ...
agp = "8.7.3"
kotlin = "2.1.0"
compose = "1.7.1"
androidx-activityCompose = "1.9.3"
ksp = "2.1.0-1.0.29"
room = "2.7.0-alpha12"
sqlite = "2.5.0-alpha12"
coroutines = "1.9.0"
lifecycle = "2.8.7"
navigation = "2.8.0-alpha10"

[libraries]
# ... keep your existing libraries ...

# Room Database
room-runtime = { module = "androidx.room:room-runtime", version.ref = "room" }
room-compiler = { module = "androidx.room:room-compiler", version.ref = "room" }
room-ktx = { module = "androidx.room:room-ktx", version.ref = "room" }
sqlite-bundled = { module = "androidx.sqlite:sqlite-bundled", version.ref = "sqlite" }

# Coroutines
kotlinx-coroutines-core = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-core", version.ref = "coroutines" }
kotlinx-coroutines-android = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-android", version.ref = "coroutines" }
kotlinx-coroutinesSwing = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-swing", version.ref = "coroutines" }

# Lifecycle
androidx-lifecycle-viewmodelCompose = { module = "androidx.lifecycle:lifecycle-viewmodel-compose", version.ref = "lifecycle" }
androidx-lifecycle-runtimeCompose = { module = "androidx.lifecycle:lifecycle-runtime-compose", version.ref = "lifecycle" }

# Navigation
navigation-compose = { module = "org.jetbrains.androidx.navigation:navigation-compose", version.ref = "navigation" }

# Activity
androidx-activity-compose = { module = "androidx.activity:activity-compose", version.ref = "androidx-activityCompose" }

[plugins]
# ... keep your existing plugins ...
androidApplication = { id = "com.android.application", version.ref = "agp" }
kotlinMultiplatform = { id = "org.jetbrains.kotlin.multiplatform", version.ref = "kotlin" }
composeMultiplatform = { id = "org.jetbrains.compose", version.ref = "compose" }
composeCompiler = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
room = { id = "androidx.room", version.ref = "room" }
```

### 2.2 Update `composeApp/build.gradle.kts`

Replace your entire `composeApp/build.gradle.kts` with:

```kotlin
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    jvm("desktop")
    
    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.kotlinx.coroutines.android)
        }
        
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            
            // Room Database
            implementation(libs.room.runtime)
            implementation(libs.sqlite.bundled)
            
            // Coroutines
            implementation(libs.kotlinx.coroutines.core)
            
            // Navigation
            implementation(libs.navigation.compose)
        }
        
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.kotlinx.coroutinesSwing)
            }
        }
        
        iosMain.dependencies {
            // iOS specific dependencies
        }
    }
}

android {
    namespace = "org.bumho.ktodo"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "org.bumho.ktodo"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
    
    // KSP for Room - all platforms
    add("kspAndroid", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
    add("kspDesktop", libs.room.compiler)
}

room {
    schemaDirectory("$projectDir/schemas")
}

compose.desktop {
    application {
        mainClass = "org.bumho.ktodo.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "KTodo"
            packageVersion = "1.0.0"
            
            windows {
                menuGroup = "KTodo"
                upgradeUuid = "B1C2D3E4-F5A6-7890-BCDE-FA1234567890"
            }
        }
    }
}
```

### 2.3 Sync Gradle

Click **Sync Now** or `File → Sync Project with Gradle Files`

---

## Step 3: Create Package Structure

In `composeApp/src/commonMain/kotlin/org/bumho/ktodo/`, create these folders:

```
org.bumho.ktodo/
├── data/           # Database, DAOs, Entities
├── domain/         # Business logic, Models, Repository
├── presentation/   # MVI State Management
└── ui/             # Compose UI screens
```

---

## Step 4: Create Data Layer (Room Database)

### 4.1 Create Todo Entity

Create `composeApp/src/commonMain/kotlin/org/bumho/ktodo/data/TodoEntity.kt`:

```kotlin
package org.bumho.ktodo.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todos")
data class TodoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val isCompleted: Boolean = false,
    val imagePath: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
```

### 4.2 Create DAO (Data Access Object)

Create `composeApp/src/commonMain/kotlin/org/bumho/ktodo/data/TodoDao.kt`:

```kotlin
package org.bumho.ktodo.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("SELECT * FROM todos ORDER BY createdAt DESC")
    fun getAllTodos(): Flow<List<TodoEntity>>
    
    @Query("SELECT * FROM todos WHERE id = :id")
    suspend fun getTodoById(id: Long): TodoEntity?
    
    @Insert
    suspend fun insertTodo(todo: TodoEntity): Long
    
    @Update
    suspend fun updateTodo(todo: TodoEntity)
    
    @Delete
    suspend fun deleteTodo(todo: TodoEntity)
    
    @Query("DELETE FROM todos WHERE id = :id")
    suspend fun deleteTodoById(id: Long)
    
    @Query("SELECT * FROM todos WHERE isCompleted = :completed ORDER BY createdAt DESC")
    fun getTodosByStatus(completed: Boolean): Flow<List<TodoEntity>>
}
```

### 4.3 Create Room Database

Create `composeApp/src/commonMain/kotlin/org/bumho/ktodo/data/TodoDatabase.kt`:

```kotlin
package org.bumho.ktodo.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [TodoEntity::class],
    version = 1,
    exportSchema = true
)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
}
```

### 4.4 Create Database Builder (Common)

Create `composeApp/src/commonMain/kotlin/org/bumho/ktodo/data/DatabaseBuilder.kt`:

```kotlin
package org.bumho.ktodo.data

import androidx.room.RoomDatabase

expect class DatabaseBuilder {
    fun build(): TodoDatabase
}

fun getDatabaseBuilder(): DatabaseBuilder = DatabaseBuilder()
```

### 4.5 Create Platform-Specific Implementations

**Android** - Create `composeApp/src/androidMain/kotlin/org/bumho/ktodo/data/DatabaseBuilder.android.kt`:

```kotlin
package org.bumho.ktodo.data

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

actual class DatabaseBuilder(private val context: Context) {
    actual fun build(): TodoDatabase {
        val dbFile = context.getDatabasePath("todo.db")
        return Room.databaseBuilder<TodoDatabase>(
            context = context,
            name = dbFile.absolutePath
        )
        .setDriver(BundledSQLiteDriver())
        .build()
    }
}
```

**Desktop** - Create `composeApp/src/desktopMain/kotlin/org/bumho/ktodo/data/DatabaseBuilder.desktop.kt`:

```kotlin
package org.bumho.ktodo.data

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import java.io.File

actual class DatabaseBuilder {
    actual fun build(): TodoDatabase {
        val dbFile = File(System.getProperty("java.io.tmpdir"), "todo.db")
        return Room.databaseBuilder<TodoDatabase>(
            name = dbFile.absolutePath,
        )
        .setDriver(BundledSQLiteDriver())
        .build()
    }
}
```

**iOS** - Create `composeApp/src/iosMain/kotlin/org/bumho/ktodo/data/DatabaseBuilder.ios.kt`:

```kotlin
package org.bumho.ktodo.data

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import platform.Foundation.NSHomeDirectory

actual class DatabaseBuilder {
    actual fun build(): TodoDatabase {
        val dbFilePath = NSHomeDirectory() + "/todo.db"
        return Room.databaseBuilder<TodoDatabase>(
            name = dbFilePath,
        )
        .setDriver(BundledSQLiteDriver())
        .build()
    }
}
```

---

## Step 5: Create Domain Layer

### 5.1 Create Domain Model

Create `composeApp/src/commonMain/kotlin/org/bumho/ktodo/domain/Todo.kt`:

```kotlin
package org.bumho.ktodo.domain

data class Todo(
    val id: Long = 0,
    val title: String,
    val description: String,
    val isCompleted: Boolean = false,
    val imagePath: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
```

### 5.2 Create Repository

Create `composeApp/src/commonMain/kotlin/org/bumho/ktodo/domain/TodoRepository.kt`:

```kotlin
package org.bumho.ktodo.domain

import org.bumho.ktodo.data.TodoDao
import org.bumho.ktodo.data.TodoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TodoRepository(private val dao: TodoDao) {
    
    fun getAllTodos(): Flow<List<Todo>> {
        return dao.getAllTodos().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    fun getTodosByStatus(completed: Boolean): Flow<List<Todo>> {
        return dao.getTodosByStatus(completed).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    suspend fun getTodoById(id: Long): Todo? {
        return dao.getTodoById(id)?.toDomain()
    }
    
    suspend fun insertTodo(todo: Todo): Long {
        return dao.insertTodo(todo.toEntity())
    }
    
    suspend fun updateTodo(todo: Todo) {
        dao.updateTodo(todo.toEntity())
    }
    
    suspend fun deleteTodo(todo: Todo) {
        dao.deleteTodo(todo.toEntity())
    }
    
    suspend fun deleteTodoById(id: Long) {
        dao.deleteTodoById(id)
    }
    
    private fun TodoEntity.toDomain(): Todo {
        return Todo(
            id = id,
            title = title,
            description = description,
            isCompleted = isCompleted,
            imagePath = imagePath,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
    
    private fun Todo.toEntity(): TodoEntity {
        return TodoEntity(
            id = id,
            title = title,
            description = description,
            isCompleted = isCompleted,
            imagePath = imagePath,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
```

---

## Step 6: Create MVI Presentation Layer

### 6.1 Create Base ViewModel

Create `composeApp/src/commonMain/kotlin/org/bumho/ktodo/presentation/BaseViewModel.kt`:

```kotlin
package org.bumho.ktodo.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseViewModel<State, Event, Effect> {
    
    protected val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    private val _state = MutableStateFlow(initialState())
    val state: StateFlow<State> = _state.asStateFlow()
    
    private val _effect = MutableSharedFlow<Effect>()
    val effect: SharedFlow<Effect> = _effect.asSharedFlow()
    
    protected abstract fun initialState(): State
    
    abstract fun onEvent(event: Event)
    
    protected fun setState(reducer: State.() -> State) {
        _state.value = _state.value.reducer()
    }
    
    protected suspend fun setEffect(effect: Effect) {
        _effect.emit(effect)
    }
    
    fun onCleared() {
        viewModelScope.cancel()
    }
}
```

### 6.2 Create MVI Contracts

Create `composeApp/src/commonMain/kotlin/org/bumho/ktodo/presentation/TodoContract.kt`:

```kotlin
package org.bumho.ktodo.presentation

import org.bumho.ktodo.domain.Todo

// State - represents UI state
data class TodoState(
    val todos: List<Todo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedTodo: Todo? = null,
    val filter: TodoFilter = TodoFilter.ALL
)

enum class TodoFilter {
    ALL, ACTIVE, COMPLETED
}

// Events - user actions
sealed class TodoEvent {
    data class LoadTodos(val forceRefresh: Boolean = false) : TodoEvent()
    data class AddTodo(val title: String, val description: String, val imagePath: String?) : TodoEvent()
    data class UpdateTodo(val todo: Todo) : TodoEvent()
    data class DeleteTodo(val todo: Todo) : TodoEvent()
    data class ToggleTodoComplete(val todo: Todo) : TodoEvent()
    data class SelectTodo(val todo: Todo?) : TodoEvent()
    data class SetFilter(val filter: TodoFilter) : TodoEvent()
}

// Effects - one-time events
sealed class TodoEffect {
    data class ShowError(val message: String) : TodoEffect()
    data object TodoAdded : TodoEffect()
    data object TodoUpdated : TodoEffect()
    data object TodoDeleted : TodoEffect()
    data class NavigateToDetail(val todoId: Long) : TodoEffect()
}
```

### 6.3 Create TodoViewModel

Create `composeApp/src/commonMain/kotlin/org/bumho/ktodo/presentation/TodoViewModel.kt`:

```kotlin
package org.bumho.ktodo.presentation

import org.bumho.ktodo.domain.Todo
import org.bumho.ktodo.domain.TodoRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class TodoViewModel(
    private val repository: TodoRepository
) : BaseViewModel<TodoState, TodoEvent, TodoEffect>() {
    
    init {
        onEvent(TodoEvent.LoadTodos())
    }
    
    override fun initialState(): TodoState = TodoState()
    
    override fun onEvent(event: TodoEvent) {
        when (event) {
            is TodoEvent.LoadTodos -> loadTodos()
            is TodoEvent.AddTodo -> addTodo(event.title, event.description, event.imagePath)
            is TodoEvent.UpdateTodo -> updateTodo(event.todo)
            is TodoEvent.DeleteTodo -> deleteTodo(event.todo)
            is TodoEvent.ToggleTodoComplete -> toggleComplete(event.todo)
            is TodoEvent.SelectTodo -> selectTodo(event.todo)
            is TodoEvent.SetFilter -> setFilter(event.filter)
        }
    }
    
    private fun loadTodos() {
        setState { copy(isLoading = true) }
        
        repository.getAllTodos()
            .onEach { todos ->
                setState {
                    copy(
                        todos = filterTodos(todos, filter),
                        isLoading = false,
                        error = null
                    )
                }
            }
            .catch { error ->
                setState {
                    copy(
                        isLoading = false,
                        error = error.message
                    )
                }
                setEffect(TodoEffect.ShowError(error.message ?: "Unknown error"))
            }
            .launchIn(viewModelScope)
    }
    
    private fun addTodo(title: String, description: String, imagePath: String?) {
        viewModelScope.launch {
            try {
                val todo = Todo(
                    title = title,
                    description = description,
                    imagePath = imagePath
                )
                repository.insertTodo(todo)
                setEffect(TodoEffect.TodoAdded)
            } catch (e: Exception) {
                setEffect(TodoEffect.ShowError(e.message ?: "Failed to add todo"))
            }
        }
    }
    
    private fun updateTodo(todo: Todo) {
        viewModelScope.launch {
            try {
                repository.updateTodo(todo.copy(updatedAt = System.currentTimeMillis()))
                setEffect(TodoEffect.TodoUpdated)
            } catch (e: Exception) {
                setEffect(TodoEffect.ShowError(e.message ?: "Failed to update todo"))
            }
        }
    }
    
    private fun deleteTodo(todo: Todo) {
        viewModelScope.launch {
            try {
                repository.deleteTodo(todo)
                setEffect(TodoEffect.TodoDeleted)
            } catch (e: Exception) {
                setEffect(TodoEffect.ShowError(e.message ?: "Failed to delete todo"))
            }
        }
    }
    
    private fun toggleComplete(todo: Todo) {
        viewModelScope.launch {
            try {
                val updated = todo.copy(
                    isCompleted = !todo.isCompleted,
                    updatedAt = System.currentTimeMillis()
                )
                repository.updateTodo(updated)
            } catch (e: Exception) {
                setEffect(TodoEffect.ShowError(e.message ?: "Failed to toggle todo"))
            }
        }
    }
    
    private fun selectTodo(todo: Todo?) {
        setState { copy(selectedTodo = todo) }
    }
    
    private fun setFilter(filter: TodoFilter) {
        setState { 
            copy(
                filter = filter,
                todos = filterTodos(todos, filter)
            )
        }
    }
    
    private fun filterTodos(todos: List<Todo>, filter: TodoFilter): List<Todo> {
        return when (filter) {
            TodoFilter.ALL -> todos
            TodoFilter.ACTIVE -> todos.filter { !it.isCompleted }
            TodoFilter.COMPLETED -> todos.filter { it.isCompleted }
        }
    }
}
```

---

## Step 7: Create Compose UI

### 7.1 Create TodoListScreen

Create `composeApp/src/commonMain/kotlin/org/bumho/ktodo/ui/TodoListScreen.kt`:

```kotlin
package org.bumho.ktodo.ui

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
import androidx.compose.ui.unit.dp
import org.bumho.ktodo.domain.Todo
import org.bumho.ktodo.presentation.TodoEffect
import org.bumho.ktodo.presentation.TodoEvent
import org.bumho.ktodo.presentation.TodoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    viewModel: TodoViewModel,
    onAddClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }
    
    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
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
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Todo")
            }
        },
        snackbarHost = {
            if (showSnackbar) {
                Snackbar(
                    action = {
                        TextButton(onClick = { showSnackbar = false }) {
                            Text("Dismiss")
                        }
                    }
                ) {
                    Text(snackbarMessage)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (state.isLoading && state.todos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (state.todos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No todos yet. Add one!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.todos, key = { it.id }) { todo ->
                        TodoItem(
                            todo = todo,
                            onToggle = { viewModel.onEvent(TodoEvent.ToggleTodoComplete(todo)) },
                            onDelete = { viewModel.onEvent(TodoEvent.DeleteTodo(todo)) },
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
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = todo.isCompleted,
                onCheckedChange = { onToggle() }
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = todo.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (todo.isCompleted) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                if (todo.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = todo.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }
            }
            
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
```

### 7.2 Create AddTodoScreen

Create `composeApp/src/commonMain/kotlin/org/bumho/ktodo/ui/AddTodoScreen.kt`:

```kotlin
package org.bumho.ktodo.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.bumho.ktodo.presentation.TodoEffect
import org.bumho.ktodo.presentation.TodoEvent
import org.bumho.ktodo.presentation.TodoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTodoScreen(
    viewModel: TodoViewModel,
    onNavigateBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
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
            if (showError) {
                Snackbar(
                    action = {
                        TextButton(onClick = { showError = false }) {
                            Text("Dismiss")
                        }
                    }
                ) {
                    Text(errorMessage)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                maxLines = 5
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        viewModel.onEvent(
                            TodoEvent.AddTodo(
                                title = title.trim(),
                                description = description.trim(),
                                imagePath = null
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank()
            ) {
                Text("Add Todo")
            }
        }
    }
}
```

### 7.3 Create App Entry Point

Create `composeApp/src/commonMain/kotlin/org/bumho/ktodo/ui/App.kt`:

```kotlin
package org.bumho.ktodo.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.bumho.ktodo.presentation.TodoViewModel

@Composable
fun App(viewModel: TodoViewModel) {
    MaterialTheme {
        val navController = rememberNavController()
        
        NavHost(
            navController = navController,
            startDestination = "todo_list"
        ) {
            composable("todo_list") {
                TodoListScreen(
                    viewModel = viewModel,
                    onAddClick = { navController.navigate("add_todo") }
                )
            }
            
            composable("add_todo") {
                AddTodoScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
```

---

## Step 8: Configure Platform-Specific Entry Points

### 8.1 Android MainActivity

Update `composeApp/src/androidMain/kotlin/org/bumho/ktodo/MainActivity.kt`:

```kotlin
package org.bumho.ktodo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import org.bumho.ktodo.data.DatabaseBuilder
import org.bumho.ktodo.domain.TodoRepository
import org.bumho.ktodo.presentation.TodoViewModel
import org.bumho.ktodo.ui.App

class MainActivity : ComponentActivity() {
    
    private lateinit var viewModel: TodoViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize database and repository
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
```

### 8.2 Desktop Main Function

Update or create `composeApp/src/desktopMain/kotlin/org/bumho/ktodo/main.kt`:

```kotlin
package org.bumho.ktodo

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.bumho.ktodo.data.DatabaseBuilder
import org.bumho.ktodo.domain.TodoRepository
import org.bumho.ktodo.presentation.TodoViewModel
import org.bumho.ktodo.ui.App

fun main() = application {
    val database = DatabaseBuilder().build()
    val repository = TodoRepository(database.todoDao())
    val viewModel = TodoViewModel(repository)
    
    Window(
        onCloseRequest = {
            viewModel.onCleared()
            exitApplication()
        },
        title = "KTodo - Multiplatform Todo App",
        state = rememberWindowState(width = androidx.compose.ui.unit.dp(400), height = androidx.compose.ui.unit.dp(700))
    ) {
        App(viewModel)
    }
}
```

### 8.3 iOS Main (Optional - requires macOS)

If you're on macOS and want to build for iOS, create `composeApp/src/iosMain/kotlin/org/bumho/ktodo/MainViewController.kt`:

```kotlin
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
```

---

## Step 9: Build and Run

### 9.1 Sync Gradle
1. Click **Sync Now** or `File → Sync Project with Gradle Files`
2. Wait for all dependencies to download

### 9.2 Run on Android
1. Select `composeApp` run configuration
2. Choose your emulator or connected device
3. Click **Run** (green play button)

### 9.3 Run on Desktop
**Option A: Using Run Configuration**
1. Go to `Run → Edit Configurations`
2. Click `+` → `Gradle`
3. Name: "Desktop App"
4. Gradle project: Select `composeApp`
5. Tasks: `desktopRun`
6. Click **OK** and **Run**

**Option B: Using Terminal**
```bash
./gradlew :composeApp:desktopRun
```

### 9.4 Run on iOS (macOS only)
1. Open `iosApp/iosApp.xcodeproj` in Xcode
2. Select target device/simulator
3. Click **Run**

---

## Step 10: Testing Your App

### Test Checklist
- ✅ Add a new todo with title only
- ✅ Add a todo with title and description
- ✅ Toggle todo completion status
- ✅ Delete a todo
- ✅ App persists data after restart
- ✅ Long descriptions display correctly
- ✅ Empty state shows when no todos
- ✅ Navigation works (list ↔ add screen)

---

## Step 11: Adding Image Support (Android Only)

### 11.1 Add Coil Dependency

Update `composeApp/build.gradle.kts`, add to `androidMain.dependencies`:

```kotlin
androidMain.dependencies {
    implementation(compose.preview)
    implementation(libs.androidx.activity.compose)
    implementation(libs.kotlinx.coroutines.android)
    implementation("io.coil-kt:coil-compose:2.5.0")  // Add this line
}
```

### 11.2 Update AddTodoScreen with Image Picker

Replace the entire `AddTodoScreen.kt` with this version that includes image picking:

```kotlin
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
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<String?>(null) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
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
            if (showError) {
                Snackbar(
                    action = {
                        TextButton(onClick = { showError = false }) {
                            Text("Dismiss")
                        }
                    }
                ) {
                    Text(errorMessage)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                maxLines = 5
            )
            
            // Image picker will be added here for Android
            ImagePickerButton(
                currentImageUri = imageUri,
                onImageSelected = { imageUri = it }
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        viewModel.onEvent(
                            TodoEvent.AddTodo(
                                title = title.trim(),
                                description = description.trim(),
                                imagePath = imageUri
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank()
            ) {
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
```

### 11.3 Create Android-Specific Image Picker

Create `composeApp/src/androidMain/kotlin/org/bumho/ktodo/ui/ImagePicker.android.kt`:

```kotlin
package org.bumho.ktodo.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest

@Composable
actual fun ImagePickerButton(
    currentImageUri: String?,
    onImageSelected: (String?) -> Unit
) {
    val context = LocalContext.current
    
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        onImageSelected(uri?.toString())
    }
    
    Column {
        Button(
            onClick = { imagePickerLauncher.launch("image/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (currentImageUri == null) "Pick Image" else "Change Image")
        }
        
        currentImageUri?.let { uri ->
            Spacer(modifier = Modifier.height(8.dp))
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(context)
                        .data(uri)
                        .build()
                ),
                contentDescription = "Selected image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
        }
    }
}
```

### 11.4 Create Desktop Stub (No Image Picker)

Create `composeApp/src/desktopMain/kotlin/org/bumho/ktodo/ui/ImagePicker.desktop.kt`:

```kotlin
package org.bumho.ktodo.ui

import androidx.compose.runtime.Composable

@Composable
actual fun ImagePickerButton(
    currentImageUri: String?,
    onImageSelected: (String?) -> Unit
) {
    // Desktop image picker not implemented
    // You can add JFileChooser here if needed
}
```

### 11.5 Create iOS Stub

Create `composeApp/src/iosMain/kotlin/org/bumho/ktodo/ui/ImagePicker.ios.kt`:

```kotlin
package org.bumho.ktodo.ui

import androidx.compose.runtime.Composable

@Composable
actual fun ImagePickerButton(
    currentImageUri: String?,
    onImageSelected: (String?) -> Unit
) {
    // iOS image picker not implemented
    // You can add UIImagePickerController here if needed
}
```

### 11.6 Update TodoItem to Display Images

Update `TodoItem` in `TodoListScreen.kt` to show images:

```kotlin
@Composable
fun TodoItem(
    todo: Todo,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = todo.isCompleted,
                onCheckedChange = { onToggle() }
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = todo.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (todo.isCompleted) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                if (todo.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = todo.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }
            }
            
            // Display image thumbnail if available (Android only)
            TodoImageThumbnail(imagePath = todo.imagePath)
            
            IconButton(onClick = onDelete) {
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
```

Create `composeApp/src/androidMain/kotlin/org/bumho/ktodo/ui/TodoImageThumbnail.android.kt`:

```kotlin
package org.bumho.ktodo.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest

@Composable
actual fun TodoImageThumbnail(imagePath: String?) {
    imagePath?.let {
        Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current)
                    .data(it)
                    .build()
            ),
            contentDescription = "Todo image",
            modifier = Modifier
                .size(60.dp)
                .padding(start = 8.dp),
            contentScale = ContentScale.Crop
        )
    }
}
```

Create stubs for desktop and iOS:

`composeApp/src/desktopMain/kotlin/org/bumho/ktodo/ui/TodoImageThumbnail.desktop.kt`:
```kotlin
package org.bumho.ktodo.ui

import androidx.compose.runtime.Composable

@Composable
actual fun TodoImageThumbnail(imagePath: String?) {
    // No image display on desktop
}
```

`composeApp/src/iosMain/kotlin/org/bumho/ktodo/ui/TodoImageThumbnail.ios.kt`:
```kotlin
package org.bumho.ktodo.ui

import androidx.compose.runtime.Composable

@Composable
actual fun TodoImageThumbnail(imagePath: String?) {
    // No image display on iOS
}
```

---

## Step 12: Common Issues & Solutions

### Issue 1: KSP Not Generating Code
**Solution**:
```bash
./gradlew clean
./gradlew :composeApp:kspCommonMainKotlinMetadata
./gradlew build
```

### Issue 2: Room Schema Error
**Solution**: Ensure `schemas` directory exists:
```bash
mkdir -p composeApp/schemas
```

### Issue 3: Gradle Sync Fails
**Solution**:
1. `File → Invalidate Caches → Invalidate and Restart`
2. Delete `.gradle` and `.idea` folders
3. Sync again

### Issue 4: Desktop App Won't Run
**Solution**: Make sure you're using the correct task:
```bash
./gradlew :composeApp:desktopRun
```

### Issue 5: Android Build Error - Duplicate Classes
**Solution**: Check for conflicting dependencies in `build.gradle.kts`

### Issue 6: Database Not Persisting
**Solution**: Check platform-specific `DatabaseBuilder` implementations are correct

---

## Step 13: Project Structure Summary

```
KTodo/
├── composeApp/
│   ├── src/
│   │   ├── commonMain/kotlin/org/bumho/ktodo/
│   │   │   ├── data/
│   │   │   │   ├── TodoEntity.kt
│   │   │   │   ├── TodoDao.kt
│   │   │   │   ├── TodoDatabase.kt
│   │   │   │   └── DatabaseBuilder.kt
│   │   │   ├── domain/
│   │   │   │   ├── Todo.kt
│   │   │   │   └── TodoRepository.kt
│   │   │   ├── presentation/
│   │   │   │   ├── BaseViewModel.kt
│   │   │   │   ├── TodoContract.kt
│   │   │   │   └── TodoViewModel.kt
│   │   │   └── ui/
│   │   │       ├── App.kt
│   │   │       ├── TodoListScreen.kt
│   │   │       └── AddTodoScreen.kt
│   │   ├── androidMain/kotlin/org/bumho/ktodo/
│   │   │   ├── MainActivity.kt
│   │   │   └── data/DatabaseBuilder.android.kt
│   │   ├── desktopMain/kotlin/org/bumho/ktodo/
│   │   │   ├── main.kt
│   │   │   └── data/DatabaseBuilder.desktop.kt
│   │   └── iosMain/kotlin/org/bumho/ktodo/
│   │       ├── MainViewController.kt
│   │       └── data/DatabaseBuilder.ios.kt
│   ├── build.gradle.kts
│   └── schemas/
├── gradle/
│   └── libs.versions.toml
├── build.gradle.kts
└── settings.gradle.kts
```

---

## Step 14: Next Steps & Improvements

### Features to Add
1. **Search and Filter**
    - Add search bar
    - Filter by status (All/Active/Completed)
    - Sort options

2. **Categories/Tags**
    - Add category entity
    - Multi-select tags
    - Filter by category

3. **Due Dates**
    - Add date picker
    - Show overdue items
    - Notification reminders

4. **Dark Mode**
    - Implement theme switching
    - Persist theme preference

5. **Export/Import**
    - Export to JSON
    - Import from JSON
    - Share functionality

6. **Cloud Sync**
    - Firebase/Supabase integration
    - Multi-device sync

### Architecture Improvements
1. **Dependency Injection** - Add Koin for DI
2. **Testing** - Unit tests, UI tests
3. **Error Handling** - Better error states
4. **Loading States** - Skeleton screens
5. **Offline Support** - Conflict resolution
6. **Performance** - Pagination, lazy loading
7. **Accessibility** - Screen reader support

---

## Step 15: Troubleshooting Commands

```bash
# Clean build
./gradlew clean

# Rebuild project
./gradlew build --refresh-dependencies

# Run Android
./gradlew :composeApp:installDebug

# Run Desktop
./gradlew :composeApp:desktopRun

# Check dependencies
./gradlew :composeApp:dependencies

# Generate Room schemas
./gradlew :composeApp:kspCommonMainKotlinMetadata
```

---

## Resources

- [Kotlin Multiplatform Documentation](https://kotlinlang.org/docs/multiplatform.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Room KMP](https://developer.android.com/kotlin/multiplatform/room)
- [MVI Architecture Pattern](https://hannesdorfmann.com/android/model-view-intent/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)

---

## Conclusion

You now have a fully functional Kotlin Multiplatform Todo app with:
- ✅ Cross-platform support (Android, iOS, Desktop)
- ✅ Room database for local storage
- ✅ MVI architecture for clean state management
- ✅ Compose Multiplatform for shared UI
- ✅ Image support (Android)
- ✅ Navigation between screens

**Happy coding! 🚀**