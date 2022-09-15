package com.spotgym.spot.data

import com.spotgym.spot.data.room.ExerciseDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExerciseRepository @Inject constructor(private val exerciseDao: ExerciseDao) {

    suspend fun getAllExercises(): List<Exercise> = exerciseDao.getAll()

    suspend fun getExercise(id: Int): Exercise? = exerciseDao.getById(id)

    suspend fun getRoutineWithExercises(routineId: Int): RoutineWithExercises? =
        exerciseDao.getRoutineWithExercises(routineId)

    suspend fun addExercise(exercise: Exercise) {
        exerciseDao.insert(exercise)
    }

    suspend fun updateExercise(exercise: Exercise) {
        exerciseDao.update(exercise)
    }

    suspend fun deleteExercise(exercise: Exercise) {
        exerciseDao.delete(exercise)
    }
}
