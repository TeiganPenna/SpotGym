package com.spotgym.spot.data.room

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.spotgym.spot.data.Exercise
import com.spotgym.spot.data.RoutineWithExercises

@Dao
interface ExerciseDao : DataDao<Exercise> {

    @Query("select * from exercises")
    suspend fun getAll(): List<Exercise>

    @Query("select * from exercises where id = :id LIMIT 1")
    suspend fun getById(id: Int): Exercise?

    @Transaction
    @Query("select * from routines where id = :routineId LIMIT 1")
    suspend fun getRoutineWithExercises(routineId: Int): RoutineWithExercises?
}
