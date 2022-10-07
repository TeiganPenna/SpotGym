package com.spotgym.spot.data

import androidx.room.Embedded
import androidx.room.Relation

data class RoutineWithExercises(
    @Embedded
    val routine: Routine,

    @Relation(parentColumn = "id", entityColumn = "routineId")
    private val exercises: List<Exercise> = emptyList(),
) {
    fun getOrderedExercises(): List<Exercise> {
        return exercises.sortedBy { it.index }
    }
}
