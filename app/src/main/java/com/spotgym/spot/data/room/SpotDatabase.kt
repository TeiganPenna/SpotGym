package com.spotgym.spot.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.spotgym.spot.data.Routine

@Database(entities = [(Routine::class)], version = 1)
abstract class SpotDatabase : RoomDatabase() {
    abstract fun routineDao(): RoutineDao

    companion object {
        private var INSTANCE: SpotDatabase? = null

        fun getInstance(context: Context): SpotDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SpotDatabase::class.java,
                        "routine_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}
