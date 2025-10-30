package org.bumho.ktodo.presentation

import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.bumho.ktodo.domain.Todo
import org.bumho.ktodo.domain.TodoRepository

class TodoViewModel(
    private val repository: TodoRepository
) : BaseViewModel<TodoState, TodoEvent, TodoEffect>() {
        init {
            onEvent(TodoEvent.LoadTodos())
        }

    override fun initialState(): TodoState = TodoState()

    override fun onEvent(event: TodoEvent) {
        when (event){
            is TodoEvent.LoadTodos -> loadTodos()
            is TodoEvent.AddTodo -> addTodo(event.title, event.description,event.imagePath)
            is TodoEvent.UpdateTodo -> updateTodo(event.todo)
            is TodoEvent.DeleteTodo -> deleteTodo(event.todo)
            is TodoEvent.ToggleTodoComplete -> toggleTodoComplete(event.todo)
            is TodoEvent.SelectTodo -> selectTodo(event.todo)
            is TodoEvent.SetFilter -> setFilter(event.filter)
        }
    }

    private fun loadTodos(){
        setState { copy(isLoading = true) }

        repository.getAllTodos()
            .onEach { todos ->
                setState { copy(todos = filterTodos(todos, filter), isLoading= false, error= null) }
            }.catch { error -> setState { copy( isLoading = false, error = error.message ) }
                setEffect(TodoEffect.ShowError(error.message ?: "Unknown error"))
            }.launchIn(viewModelScope)
    }

    private fun addTodo(title: String, description: String, imagePath: String?) {
        viewModelScope.launch {
            try {
                val todo = Todo(title = title, description = description, imagePath = imagePath)
                repository.insertTodo(todo)
                setEffect(TodoEffect.TodoAdded)
            }catch (e: Exception){
                setEffect(TodoEffect.ShowError(e.message ?: "Failed to add todo"))
            }
        }
    }

    private fun updateTodo(todo: Todo) {
        viewModelScope.launch {
            try {
                repository.updateTodo(todo.copy(updatedAt = System.currentTimeMillis()))
                setEffect(TodoEffect.TodoUpdated)
            }catch (e: Exception){
                setEffect(TodoEffect.ShowError(e.message ?: "Failed to update todo"))
            }
        }
    }
    private fun deleteTodo(todo: Todo){
        viewModelScope.launch {
            try {
                repository.deleteTodo(todo)
                setEffect(TodoEffect.TodoDeleted)
            }catch (e: Exception){
                setEffect(TodoEffect.ShowError(e.message ?: "Failed to delete todo"))
            }
        }
    }
    private fun toggleTodoComplete(todo: Todo){
        viewModelScope.launch {
            try {
                val updated = todo.copy(isCompleted = !todo.isCompleted, updatedAt = System.currentTimeMillis())
                repository.updateTodo(updated)
            }catch (e: Exception){
                setEffect(TodoEffect.ShowError(e.message ?: "Failed to toggle todo"))
            }
        }
    }

    private fun selectTodo(todo: Todo?){
        setState { copy(selectedTodo = todo) }
    }

    private fun setFilter(filter: TodoFilter){
        setState {
            copy(
                filter = filter, todos = filterTodos(todos, filter)
            )
        }
    }

    private fun filterTodos(todos: List<Todo>, filter: TodoFilter):List<Todo>{
        return when(filter){
            TodoFilter.ALL -> todos
            TodoFilter.ACTIVE -> todos.filter { !it.isCompleted }
            TodoFilter.COMPLETED -> todos.filter { it.isCompleted }
        }
    }
}