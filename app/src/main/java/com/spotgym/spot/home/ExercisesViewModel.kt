package com.spotgym.spot.home

import androidx.lifecycle.ViewModel
import com.spotgym.spot.data.Routine
import com.spotgym.spot.data.RoutineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ExercisesViewModel @Inject constructor(
    private val routineRepository: RoutineRepository
) : ViewModel() {
    suspend fun getRoutine(id: Int): Routine? = routineRepository.getRoutine(id)
}
