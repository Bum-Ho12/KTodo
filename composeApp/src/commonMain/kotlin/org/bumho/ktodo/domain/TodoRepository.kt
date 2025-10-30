package org.bumho.ktodo.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.bumho.ktodo.data.TodoDao
import org.bumho.ktodo.data.TodoEntity

class TodoRepository(private  val dao: TodoDao) {
    fun getAllTodos(): Flow<List<Todo>>{
        return  dao.getAllTodos().map { entities -> entities.map{it.toDomain()} }
    }

    fun getTodosByStatus(completed: Boolean): Flow<List<Todo>>{
        return dao.getTodosByStatus(completed).map { entities -> entities.map { it.toDomain() }}
    }

    suspend fun getTodoById(id: Long): Todo?{
        return dao.getTodoById(id)?.toDomain()
    }

    suspend fun insertTodo(todo: Todo): Long {
        return  dao.insertTodo(todo.toEntity())
    }

    suspend fun updateTodo(todo: Todo){
        dao.updateTodo(todo.toEntity())
    }

    suspend fun deleteTodo(todo: Todo){
        dao.deleteTodo(todo.toEntity())
    }

    suspend fun deletedTodoById(id: Long){
        dao.deleteTodoById(id)
    }

    private fun TodoEntity.toDomain():Todo{
        return Todo(
            id = id,
            title = title,
            description = description,
            isCompleted = isCompleted,
            imagePath = imagePath,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
    }

    private fun Todo.toEntity(): TodoEntity{
        return TodoEntity(
            id = id,
            title = title,
            description = description,
            isCompleted = isCompleted,
            imagePath = imagePath,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
    }
}