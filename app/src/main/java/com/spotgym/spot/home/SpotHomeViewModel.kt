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

    fun addRoutine(name: String, description: String) {
        viewModelScope.launch {
            val routine = Routine(
                name = name.trim(),
                description = description.trim(),
                index = routines.value?.size ?: 0
            )
            routineRepository.addRoutine(routine)
            loadRoutines()
        }
    }

    fun deleteRoutine(routine: Routine) {
        viewModelScope.launch {
            routineRepository.deleteRoutine(routine)
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
                ROUTINE_NAME_PROPERTY,
            )
        }
        if (description.isBlank()) {
            return ValidationResult.failure(
                context.getString(
                    R.string.validation_value_empty,
                    context.getString(R.string.routine_description)
                ),
                ROUTINE_DESCRIPTION_PROPERTY,
            )
        }
        return ValidationResult.success
    }

    companion object {
        const val ROUTINE_NAME_PROPERTY = "name"
        const val ROUTINE_DESCRIPTION_PROPERTY = "description"
    }
}
