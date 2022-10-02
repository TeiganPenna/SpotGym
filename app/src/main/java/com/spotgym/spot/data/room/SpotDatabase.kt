package com.spotgym.spot.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.spotgym.spot.data.Exercise
import com.spotgym.spot.data.Routine

@Database(entities = [(Routine::class), Exercise::class], version = 3)
abstract class SpotDatabase : RoomDatabase() {
    abstract fun routineDao(): RoutineDao

    abstract fun exerciseDao(): ExerciseDao
}
