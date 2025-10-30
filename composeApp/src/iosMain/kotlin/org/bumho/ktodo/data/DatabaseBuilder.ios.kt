@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package org.bumho.ktodo.data

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import platform.Foundation.NSHomeDirectory

actual class DatabaseBuilder {
    actual fun build(): TodoDatabase{
        val dbFilePath = NSHomeDirectory() + "/todo.db"
        return Room.databaseBuilder<TodoDatabase>(
            name = dbFilePath,
        )
            .setDriver(BundledSQLiteDriver())
            .build()
    }

}