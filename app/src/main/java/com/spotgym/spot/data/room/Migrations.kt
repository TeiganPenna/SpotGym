package com.spotgym.spot.data.room

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migrations {
    val ROUTINE_EXERCISE_INDEX_3_4 = object : Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE routines ADD COLUMN routineIndex INTEGER NOT NULL DEFAULT 0")
            database.execSQL("ALTER TABLE exercises ADD COLUMN exerciseIndex INTEGER NOT NULL DEFAULT 0")
        }
    }
}
