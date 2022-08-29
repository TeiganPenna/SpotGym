package com.spotgym.spot.di

import com.spotgym.spot.data.room.SpotDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SpotModule {

    @Singleton
    @Provides
    fun provideRoutineDao(db: SpotDatabase) = db.routineDao()
}
