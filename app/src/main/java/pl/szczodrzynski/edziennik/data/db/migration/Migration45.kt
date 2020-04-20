/*
 * Copyright (c) Kacper Ziubryniewicz 2020-1-25
 */

package pl.szczodrzynski.edziennik.data.db.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration45 : Migration(44, 45) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("UPDATE profiles SET empty = 1")
    }
}
