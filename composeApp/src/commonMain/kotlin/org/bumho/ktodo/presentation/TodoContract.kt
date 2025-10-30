package org.bumho.ktodo.presentation

import org.bumho.ktodo.domain.Todo

data class TodoState(
    val todos: List<Todo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedTodo: Todo? = null,
    val filter: TodoFilter = TodoFilter.ALL
)

enum class TodoFilter{
    ALL, ACTIVE, COMPLETED
}

// Events - user actions
sealed class TodoEvent{
    data class LoadTodos(val forceRefresh: Boolean = false) : TodoEvent()
    data class AddTodo(val title: String, val description : String, val imagePath: String?): TodoEvent()
    data class UpdateTodo(val todo: Todo) : TodoEvent()
    data class DeleteTodo(val todo: Todo) : TodoEvent()
    data class ToggleTodoComplete(val todo: Todo) : TodoEvent()
    data class SelectTodo(val todo: Todo?) : TodoEvent()
    data class SetFilter(val filter: TodoFilter) : TodoEvent()
}

// Effects - one-time Events
sealed class TodoEffect{
    data class ShowError(val message: String): TodoEffect()
    data object TodoAdded : TodoEffect()
    data object TodoUpdated : TodoEffect()
    data object TodoDeleted : TodoEffect()
    data class NavigateToDetail(val todoId: Long) : TodoEffect()
}