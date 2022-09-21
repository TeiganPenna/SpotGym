package com.spotgym.spot

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import com.spotgym.spot.data.Exercise
import com.spotgym.spot.data.ExerciseRepository
import com.spotgym.spot.data.Routine
import com.spotgym.spot.data.RoutineWithExercises
import com.spotgym.spot.home.ExercisesViewModel
import com.spotgym.spot.ui.service.ToastService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class ExercisesViewModelTest {

    private lateinit var viewModel: ExercisesViewModel

    @Mock
    private lateinit var context: Context
    @Mock
    private lateinit var repositoryMock: ExerciseRepository
    @Mock
    private lateinit var toastServiceMock: ToastService

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun before() {
        whenever(context.getString(R.string.exercises_error_findroutine)).thenReturn("some error message")
        whenever(context.getString(R.string.exercise_name)).thenReturn("name")
        whenever(context.getString(R.string.exercise_description)).thenReturn("description")
        whenever(context.getString(eq(R.string.validation_value_empty), any())).then {
            "some error message " + it.arguments[1]
        }

        viewModel = ExercisesViewModel(repositoryMock, toastServiceMock)
    }

    @Test
    fun `routineData is null initially`() {
        assertThat(viewModel.routineData).isNull()
    }

    @Test
    fun `routineData is null after loading`() = runTest {
        viewModel.loadRoutineData(context, TEST_ROUTINE_ID)

        val loadedData = viewModel.routineData
        assertThat(loadedData).isNull()
        verify(toastServiceMock).showText(context, "some error message", Toast.LENGTH_LONG)
    }

    @Test
    fun `routineData is loaded`() = runTest {
        val routine = Routine(TEST_ROUTINE_ID, "Some Routine", "Some Description")
        val exercises = listOf(
            Exercise(name = "Exercise 1", description = "Description 1", routineId = routine.id),
            Exercise(name = "Exercise 2", description = "Description 2", routineId = routine.id),
            Exercise(name = "Exercise 3", description = "Description 3", routineId = routine.id),
        )
        val data = RoutineWithExercises(routine, exercises)
        whenever(repositoryMock.getRoutineWithExercises(TEST_ROUTINE_ID)).thenReturn(data)

        viewModel.loadRoutineData(context, TEST_ROUTINE_ID)

        val loadedData = viewModel.routineData
        assertThat(loadedData).isNotNull
        assertThat(loadedData!!.routine.name).isEqualTo("Some Routine")
        assertThat(loadedData.exercises).hasSize(3)
    }

    @Test
    fun `addExercise adds to repository and reloads`() = runTest {
        val routine = Routine(TEST_ROUTINE_ID, "Some Routine", "Some Description")
        val exercises = mutableListOf<Exercise>()
        val data = RoutineWithExercises(routine, exercises)
        whenever(repositoryMock.getRoutineWithExercises(TEST_ROUTINE_ID)).thenReturn(data)
        whenever(repositoryMock.addExercise(any())).then {
            exercises.add(it.arguments[0] as Exercise)
        }

        viewModel.loadRoutineData(context, TEST_ROUTINE_ID)

        val loadedData = viewModel.routineData
        assertThat(loadedData!!.exercises).isEmpty()

        val exercise = Exercise(name = "Some Exercise", description = "some description", routineId = TEST_ROUTINE_ID)
        viewModel.addExercise(context, TEST_ROUTINE_ID, exercise)

        verify(repositoryMock).addExercise(exercise)

        assertThat(loadedData.exercises).hasSize(1)
        assertThat(loadedData.exercises[0]).isEqualTo(exercise)
    }

    @Test
    fun `validateExercise succeeds for complete exercise`() = runTest {
        val nameIsError = mutableStateOf(false)
        val descriptionIsError = mutableStateOf(false)
        val result = viewModel.validateExercise(
            context,
            mutableStateOf("name"),
            nameIsError,
            mutableStateOf("description"),
            descriptionIsError,
        )

        assertThat(result.isSuccess).isTrue
        assertThat(result.message).isNull()
        assertThat(nameIsError.value).isFalse
        assertThat(descriptionIsError.value).isFalse
    }

    @Test
    fun `validateExercise fails when name is empty`() = runTest {
        val nameIsError = mutableStateOf(false)
        val descriptionIsError = mutableStateOf(false)
        val result = viewModel.validateExercise(
            context,
            mutableStateOf(""),
            nameIsError,
            mutableStateOf("description"),
            descriptionIsError,
        )

        assertThat(result.isSuccess).isFalse
        assertThat(result.message).isEqualTo("some error message name")
        assertThat(nameIsError.value).isTrue
        assertThat(descriptionIsError.value).isFalse
    }

    @Test
    fun `validateExercise fails when description is empty`() = runTest {
        val nameIsError = mutableStateOf(false)
        val descriptionIsError = mutableStateOf(false)
        val result = viewModel.validateExercise(
            context,
            mutableStateOf("name"),
            nameIsError,
            mutableStateOf(""),
            descriptionIsError,
        )

        assertThat(result.isSuccess).isFalse
        assertThat(result.message).isEqualTo("some error message description")
        assertThat(nameIsError.value).isFalse
        assertThat(descriptionIsError.value).isTrue
    }

    @Test
    fun `validateExercise resets nameIsError when name is ok`() = runTest {
        val nameIsError = mutableStateOf(true)
        val descriptionIsError = mutableStateOf(false)
        val result = viewModel.validateExercise(
            context,
            mutableStateOf("name"),
            nameIsError,
            mutableStateOf("description"),
            descriptionIsError,
        )

        assertThat(result.isSuccess).isTrue
        assertThat(result.message).isNull()
        assertThat(nameIsError.value).isFalse
        assertThat(descriptionIsError.value).isFalse
    }

    @Test
    fun `validateExercise resets descriptionIsError when description is ok`() = runTest {
        val nameIsError = mutableStateOf(false)
        val descriptionIsError = mutableStateOf(true)
        val result = viewModel.validateExercise(
            context,
            mutableStateOf("name"),
            nameIsError,
            mutableStateOf("description"),
            descriptionIsError,
        )

        assertThat(result.isSuccess).isTrue
        assertThat(result.message).isNull()
        assertThat(nameIsError.value).isFalse
        assertThat(descriptionIsError.value).isFalse
    }

    companion object {
        const val TEST_ROUTINE_ID = 0
    }
}
