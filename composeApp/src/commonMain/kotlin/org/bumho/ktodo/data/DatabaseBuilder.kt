@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package org.bumho.ktodo.data


expect class DatabaseBuilder {
    fun build(): TodoDatabase
}
