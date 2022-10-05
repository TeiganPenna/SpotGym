package com.spotgym.spot.di

import android.content.Context
import androidx.room.Room
import com.spotgym.spot.data.ExerciseRepository
import com.spotgym.spot.data.ExerciseRepositoryImpl
import com.spotgym.spot.data.RoutineRepository
import com.spotgym.spot.data.RoutineRepositoryImpl
import com.spotgym.spot.data.room.Migrations.ROUTINE_EXERCISE_INDEX_3_4
import com.spotgym.spot.data.room.SpotDatabase
import com.spotgym.spot.ui.service.ToastService
import com.spotgym.spot.ui.service.ToastServiceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
@SuppressWarnings("UnnecessaryAbstractClass")
abstract class SingletonModule {

    @Singleton
    @Binds
    abstract fun bindExerciseRepository(exerciseRepositoryImpl: ExerciseRepositoryImpl): ExerciseRepository

    @Singleton
    @Binds
    abstract fun bindRoutineRepository(routineRepositoryImpl: RoutineRepositoryImpl): RoutineRepository

    companion object {

        @Singleton
        @Provides
        fun provideSpotDatabase(
            @ApplicationContext context: Context
        ) = Room.databaseBuilder(
            context,
            SpotDatabase::class.java,
            "spot_database"
        ).addMigrations(ROUTINE_EXERCISE_INDEX_3_4).build()

        @Singleton
        @Provides
        fun provideExerciseDao(db: SpotDatabase) = db.exerciseDao()

        @Singleton
        @Provides
        fun provideRoutineDao(db: SpotDatabase) = db.routineDao()

        @Singleton
        @Provides
        fun provideToastService(): ToastService = ToastServiceImpl()
    }
}
