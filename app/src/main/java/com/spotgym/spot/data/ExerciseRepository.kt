package com.spotgym.spot.data

import com.spotgym.spot.data.room.ExerciseDao
import javax.inject.Inject
import javax.inject.Singleton

interface ExerciseRepository {
    suspend fun getAllExercises(): List<Exercise>
    suspend fun getExercise(id: Int): Exercise?
    suspend fun getRoutineWithExercises(routineId: Int): RoutineWithExercises?
    suspend fun addExercise(exercise: Exercise)
    suspend fun updateExercise(exercise: Exercise)
    suspend fun deleteExercise(exercise: Exercise)
}

@Singleton
class ExerciseRepositoryImpl
@Inject constructor(private val exerciseDao: ExerciseDao) : ExerciseRepository {

    override suspend fun getAllExercises(): List<Exercise> = exerciseDao.getAll()

    override suspend fun getExercise(id: Int): Exercise? = exerciseDao.getById(id)

    override suspend fun getRoutineWithExercises(routineId: Int): RoutineWithExercises? =
        exerciseDao.getRoutineWithExercises(routineId)

    override suspend fun addExercise(exercise: Exercise) {
        exerciseDao.insert(exercise)
    }

    override suspend fun updateExercise(exercise: Exercise) {
        exerciseDao.update(exercise)
    }

    override suspend fun deleteExercise(exercise: Exercise) {
        exerciseDao.delete(exercise)
    }
}
