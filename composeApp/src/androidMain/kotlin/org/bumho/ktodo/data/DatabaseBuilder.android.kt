@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package org.bumho.ktodo.data

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

actual class DatabaseBuilder(private val context: Context) {
    actual fun build(): TodoDatabase{
        val dbFile= context.getDatabasePath("todo.db")
        return Room.databaseBuilder<TodoDatabase>(
            context= context,
            name = dbFile.absolutePath
        ).setDriver(BundledSQLiteDriver())
            .build()
    }
}
