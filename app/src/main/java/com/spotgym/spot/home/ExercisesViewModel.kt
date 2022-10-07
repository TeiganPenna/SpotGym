package com.spotgym.spot.home

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spotgym.spot.R
import com.spotgym.spot.data.Exercise
import com.spotgym.spot.data.ExerciseRepository
import com.spotgym.spot.data.Routine
import com.spotgym.spot.data.RoutineWithExercises
import com.spotgym.spot.ui.service.ToastService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExercisesViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
    private val toastService: ToastService,
) : ViewModel() {

    private var routineData by mutableStateOf<RoutineWithExercises?>(null)
    private val _routine = mutableStateOf<Routine?>(null)
    private val _exercises = MutableStateFlow<List<Exercise>?>(null)
    val routine: State<Routine?> = _routine
    val exercises: StateFlow<List<Exercise>?> = _exercises

    suspend fun loadRoutineData(
        context: Context,
        routineId: Int
    ) {
        val data = exerciseRepository.getRoutineWithExercises(routineId)
        if (data == null) {
            toastService.showText(context, context.getString(R.string.exercises_error_findroutine), Toast.LENGTH_LONG)
        }
        routineData = data

        _routine.value = routineData?.routine
        _exercises.value = routineData?.getOrderedExercises()
    }

    fun addExercise(
        context: Context,
        routineId: Int,
        name: String,
        description: String,
    ) {
        viewModelScope.launch {
            val exercise = Exercise(
                name = name.trim(),
                description = description.trim(),
                routineId = routineId,
                index = exercises.value?.size ?: 0
            )
            exerciseRepository.addExercise(exercise)
            loadRoutineData(context, routineId)
        }
    }

    fun deleteExercise(
        context: Context,
        routineId: Int,
        exercise: Exercise
    ) {
        viewModelScope.launch {
            exerciseRepository.deleteExercise(exercise)
            loadRoutineData(context, routineId)
        }
    }

    fun moveExercise(fromIndex: Int, toIndex: Int) {
        _exercises.value = _exercises.value?.toMutableList()?.apply {
            add(toIndex, removeAt(fromIndex))
        }

        viewModelScope.launch {
            val exercisesToUpdate = _exercises.value
                ?.filterIndexed { index, exercise ->
                    if (exercise.index != index) {
                        exercise.index = index
                        return@filterIndexed true
                    }
                    return@filterIndexed false
                }
            if (exercisesToUpdate != null) {
                exerciseRepository.updateExercises(exercisesToUpdate)
            }
        }
    }

    @SuppressWarnings("ReturnCount")
    fun validateExercise(
        context: Context,
        name: String,
        description: String,
    ): ValidationResult {
        if (name.isBlank()) {
            return ValidationResult.failure(
                context.getString(
                    R.string.validation_value_empty,
                    context.getString(R.string.exercise_name)
                ),
                EXERCISE_NAME_PROPERTY,
            )
        }
        if (description.isBlank()) {
            return ValidationResult.failure(
                context.getString(
                    R.string.validation_value_empty,
                    context.getString(R.string.exercise_description)
                ),
                EXERCISE_DESCRIPTION_PROPERTY,
            )
        }
        return ValidationResult.success
    }

    companion object {
        const val EXERCISE_NAME_PROPERTY = "name"
        const val EXERCISE_DESCRIPTION_PROPERTY = "description"
    }
}
