package com.spotgym.spot.home

import androidx.lifecycle.ViewModel
import com.spotgym.spot.data.Routine
import com.spotgym.spot.data.RoutineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val routineRepository: RoutineRepository
) : ViewModel() {

    fun getRoutines(): Flow<List<Routine>> = routineRepository.readAllData
}
