package com.spotgym.spot.home

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.spotgym.spot.R
import com.spotgym.spot.data.ExerciseRepository
import com.spotgym.spot.data.RoutineWithExercises
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ExercisesViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    suspend fun getRoutineWithExercises(
        context: Context,
        routineId: Int
    ): RoutineWithExercises? {
        val routine = exerciseRepository.getRoutineWithExercises(routineId)
        if (routine == null) {
            Toast.makeText(context, context.getString(R.string.exercises_error_findroutine), Toast.LENGTH_LONG).show()
        }
        return routine
    }
}
