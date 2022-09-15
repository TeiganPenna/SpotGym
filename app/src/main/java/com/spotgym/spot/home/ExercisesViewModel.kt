package com.spotgym.spot.home

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.spotgym.spot.R
import com.spotgym.spot.data.Routine
import com.spotgym.spot.data.RoutineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ExercisesViewModel @Inject constructor(
    private val routineRepository: RoutineRepository
) : ViewModel() {

    suspend fun getRoutine(
        context: Context,
        routineId: Int
    ): Routine? {
        val routine = routineRepository.getRoutine(routineId)
        if (routine == null) {
            Toast.makeText(context, context.getString(R.string.exercises_error_findroutine), Toast.LENGTH_LONG).show()
        }
        return routine
    }
}
