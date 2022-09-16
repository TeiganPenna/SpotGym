package com.spotgym.spot.di

import com.spotgym.spot.data.ExerciseRepository
import com.spotgym.spot.data.ExerciseRepositoryImpl
import com.spotgym.spot.data.RoutineRepository
import com.spotgym.spot.data.RoutineRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
@SuppressWarnings("UnnecessaryAbstractClass")
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindExerciseRepository(exerciseRepositoryImpl: ExerciseRepositoryImpl): ExerciseRepository

    @Singleton
    @Binds
    abstract fun bindRoutineRepository(routineRepositoryImpl: RoutineRepositoryImpl): RoutineRepository
}
