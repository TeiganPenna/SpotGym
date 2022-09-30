package com.spotgym.spot

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.spotgym.spot.data.ExerciseRepository
import com.spotgym.spot.data.ExerciseRepositoryImpl
import com.spotgym.spot.data.RoutineRepository
import com.spotgym.spot.data.RoutineRepositoryImpl
import com.spotgym.spot.data.room.SpotDatabase
import com.spotgym.spot.di.SingletonModule
import com.spotgym.spot.ui.service.ToastService
import com.spotgym.spot.ui.service.ToastServiceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [SingletonModule::class]
)
abstract class SingletonModuleForTest {

    @Suppress("unused")
    @Singleton
    @Binds
    abstract fun bindExerciseRepository(exerciseRepositoryImpl: ExerciseRepositoryImpl): ExerciseRepository

    @Suppress("unused")
    @Singleton
    @Binds
    abstract fun bindRoutineRepository(routineRepositoryImpl: RoutineRepositoryImpl): RoutineRepository

    companion object {

        @Singleton
        @Provides
        fun provideSpotDatabase(): SpotDatabase {
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            return Room.inMemoryDatabaseBuilder(
                context,
                SpotDatabase::class.java
            )
                .allowMainThreadQueries()
                .build()
        }

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
