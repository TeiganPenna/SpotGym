package com.spotgym.spot

import android.content.Context
import android.widget.Toast
import com.spotgym.spot.data.Exercise
import com.spotgym.spot.data.ExerciseRepository
import com.spotgym.spot.data.Routine
import com.spotgym.spot.data.RoutineWithExercises
import com.spotgym.spot.home.ExercisesViewModel
import com.spotgym.spot.ui.service.ToastService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

@ExtendWith(MockKExtension::class, MainCoroutineExtension::class)
@ExperimentalCoroutinesApi
class ExercisesViewModelTest {

    private lateinit var viewModel: ExercisesViewModel

    @MockK
    private lateinit var context: Context
    @MockK
    private lateinit var repositoryMock: ExerciseRepository
    @MockK
    private lateinit var toastServiceMock: ToastService

    @BeforeEach
    fun beforeEach() {
        every { context.getString(R.string.exercises_error_findroutine) } returns "some error message"
        every { context.getString(R.string.exercise_name) } returns "name"
        every { context.getString(R.string.exercise_description) } returns "description"

        val captured = mutableListOf<Any?>()
        every {
            context.getString(eq(R.string.validation_value_empty), *varargAllNullable { captured.add(it) })
        } answers {
            val secondArg = captured[0] as String?
            captured.clear()
            "error $secondArg"
        }

        viewModel = ExercisesViewModel(repositoryMock, toastServiceMock)
    }

    @Test
    fun `routineData is null initially`() {
        assertThat(viewModel.routineData).isNull()
    }

    @Test
    fun `routineData is null after loading`() = runTest {
        coEvery { repositoryMock.getRoutineWithExercises(TEST_ROUTINE_ID) } returns null
        justRun { toastServiceMock.showText(any(), any(), any()) }

        viewModel.loadRoutineData(context, TEST_ROUTINE_ID)

        val loadedData = viewModel.routineData
        assertThat(loadedData).isNull()
        verify { toastServiceMock.showText(context, "some error message", Toast.LENGTH_LONG) }
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
        coEvery { repositoryMock.getRoutineWithExercises(TEST_ROUTINE_ID) } returns data

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
        coEvery { repositoryMock.getRoutineWithExercises(TEST_ROUTINE_ID) } returns data
        coEvery { repositoryMock.addExercise(any()) } answers {
            exercises.add(firstArg())
        }

        viewModel.loadRoutineData(context, TEST_ROUTINE_ID)

        val loadedData = viewModel.routineData
        assertThat(loadedData!!.exercises).isEmpty()

        val exercise = Exercise(name = "Some Exercise", description = "some description", routineId = TEST_ROUTINE_ID)
        viewModel.addExercise(context, TEST_ROUTINE_ID, exercise)

        coVerify { repositoryMock.addExercise(exercise) }

        assertThat(loadedData.exercises).hasSize(1)
        assertThat(loadedData.exercises[0]).isEqualTo(exercise)
    }

    @ParameterizedTest
    @MethodSource("validationTestData")
    fun `exercise validation`(
        name: String,
        description: String,
        expectedResult: Boolean,
        expectedErrorMessage: String?,
        expectedErrorProperty: String?,
    ) {
        val result = viewModel.validateExercise(
            context,
            name,
            description,
        )

        assertThat(result.isSuccess).isEqualTo(expectedResult)
        if (expectedResult) {
            assertThat(result.error).isNull()
        } else {
            assertThat(result.error!!.message).isEqualTo(expectedErrorMessage)
            assertThat(result.error!!.property).isEqualTo(expectedErrorProperty)
        }
    }

    companion object {
        const val TEST_ROUTINE_ID = 0

        @JvmStatic
        fun validationTestData() = listOf(
            Arguments.of("name", "description", true, null, null),
            Arguments.of("", "description", false, "error name", ExercisesViewModel.EXERCISE_NAME_PROPERTY),
            Arguments.of("name", "", false, "error description", ExercisesViewModel.EXERCISE_DESCRIPTION_PROPERTY),
            Arguments.of("", "", false, "error name", ExercisesViewModel.EXERCISE_NAME_PROPERTY),
        )
    }
}
