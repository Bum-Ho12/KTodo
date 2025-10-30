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

actual fun getDatabaseBuilder(): DatabaseBuilder {
    return DatabaseBuilder()
}