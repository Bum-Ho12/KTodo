package org.bumho.ktodo.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName= "todos")
data class TodoEntity(
    @PrimaryKey(autoGenerate = true)
    val  id: Long = 0,
    val title: String,
    val description: String,
    val isCompleted: Boolean = false,
    val imagePath: String?,
    val createdAt: Long =   System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
