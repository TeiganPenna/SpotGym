package com.spotgym.spot.home

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spotgym.spot.R
import com.spotgym.spot.data.Exercise
import com.spotgym.spot.data.ExerciseRepository
import com.spotgym.spot.data.RoutineWithExercises
import com.spotgym.spot.ui.service.ToastService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExercisesViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
    private val toastService: ToastService,
) : ViewModel() {

    var routineData by mutableStateOf<RoutineWithExercises?>(null)
        private set

    suspend fun loadRoutineData(
        context: Context,
        routineId: Int
    ) {
        val data = exerciseRepository.getRoutineWithExercises(routineId)
        if (data == null) {
            toastService.showText(context, context.getString(R.string.exercises_error_findroutine), Toast.LENGTH_LONG)
        }
        routineData = data
    }

    fun addExercise(
        context: Context,
        routineId: Int,
        exercise: Exercise
    ) {
        viewModelScope.launch {
            exerciseRepository.addExercise(exercise)
            loadRoutineData(context, routineId)
        }
    }

    @SuppressWarnings("ReturnCount")
    fun validateExercise(
        context: Context,
        name: MutableState<String>,
        nameIsError: MutableState<Boolean>,
        description: MutableState<String>,
        descriptionIsError: MutableState<Boolean>,
    ): ValidationResult {
        if (name.value.isBlank()) {
            nameIsError.value = true
            descriptionIsError.value = false // reset so only one field has an error at a time
            return ValidationResult(
                false,
                context.getString(
                    R.string.validation_value_empty,
                    context.getString(R.string.exercise_name)
                )
            )
        } else {
            nameIsError.value = false
        }
        if (description.value.isBlank()) {
            descriptionIsError.value = true
            return ValidationResult(
                false,
                context.getString(
                    R.string.validation_value_empty,
                    context.getString(R.string.exercise_description)
                )
            )
        } else {
            descriptionIsError.value = false
        }
        return ValidationResult(true, null)
    }
}
