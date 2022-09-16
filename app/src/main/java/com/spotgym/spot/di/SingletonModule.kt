package com.spotgym.spot.di

import android.content.Context
import androidx.room.Room
import com.spotgym.spot.data.room.SpotDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SingletonModule {

    @Singleton
    @Provides
    fun provideSpotDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        SpotDatabase::class.java,
        "spot_database"
    ).fallbackToDestructiveMigration().build()

    @Singleton
    @Provides
    fun provideExerciseDao(db: SpotDatabase) = db.exerciseDao()

    @Singleton
    @Provides
    fun provideRoutineDao(db: SpotDatabase) = db.routineDao()
}
