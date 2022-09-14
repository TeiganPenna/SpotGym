package com.spotgym.spot.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spotgym.spot.data.Routine
import com.spotgym.spot.data.RoutineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val routineRepository: RoutineRepository
) : ViewModel() {

    private val _routines = MutableStateFlow(emptyList<Routine>())
    val routines: StateFlow<List<Routine>> = _routines

    init {
        refreshRoutines()
    }

    fun refreshRoutines() {
        viewModelScope.launch {
            _routines.value = routineRepository.getAllRoutines()
        }
    }

    suspend fun getRoutine(id: Int): Routine? = routineRepository.getRoutine(id)

    suspend fun addRoutine(routine: Routine) {
        routineRepository.addRoutine(routine)
    }
}
