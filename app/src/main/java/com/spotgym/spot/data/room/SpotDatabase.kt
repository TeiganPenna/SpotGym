package com.spotgym.spot.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.spotgym.spot.data.Routine

@Database(entities = [(Routine::class)], version = 1)
abstract class SpotDatabase : RoomDatabase() {
    abstract fun routineDao(): RoutineDao
}
