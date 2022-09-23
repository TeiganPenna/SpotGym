package com.spotgym.spot.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spotgym.spot.R
import com.spotgym.spot.data.Routine
import com.spotgym.spot.data.RoutineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SpotHomeViewModel @Inject constructor(
    private val routineRepository: RoutineRepository
) : ViewModel() {

    private val _routines: MutableStateFlow<List<Routine>?> = MutableStateFlow(null)
    val routines: StateFlow<List<Routine>?> = _routines

    suspend fun loadRoutines() {
        _routines.value = routineRepository.getAllRoutines()
    }

    fun addRoutine(routine: Routine) {
        viewModelScope.launch {
            routineRepository.addRoutine(routine)
            loadRoutines()
        }
    }

    @SuppressWarnings("ReturnCount")
    fun validateRoutine(
        context: Context,
        name: String,
        description: String,
    ): ValidationResult {
        if (name.isBlank()) {
            return ValidationResult.failure(
                context.getString(
                    R.string.validation_value_empty,
                    context.getString(R.string.routine_name)
                ),
                "name"
            )
        }
        if (description.isBlank()) {
            return ValidationResult.failure(
                context.getString(
                    R.string.validation_value_empty,
                    context.getString(R.string.routine_description)
                ),
                "description"
            )
        }
        return ValidationResult.success
    }
}
