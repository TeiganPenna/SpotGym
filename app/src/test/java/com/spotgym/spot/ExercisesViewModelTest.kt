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
import io.mockk.coJustRun
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

    private val testRoutine = Routine(TEST_ROUTINE_ID, "Some Routine", "some description", 0)

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
    fun `routine and exercises are null initially`() {
        assertThat(viewModel.routine.value).isNull()
        assertThat(viewModel.exercises.value).isNull()
    }

    @Test
    fun `routineData is null after loading`() = runTest {
        coEvery { repositoryMock.getRoutineWithExercises(TEST_ROUTINE_ID) } returns null
        justRun { toastServiceMock.showText(any(), any(), any()) }

        viewModel.loadRoutineData(context, TEST_ROUTINE_ID)

        val loadedRoutine = viewModel.routine.value
        assertThat(loadedRoutine).isNull()
        verify { toastServiceMock.showText(context, "some error message", Toast.LENGTH_LONG) }
    }

    @Test
    fun `routineData is loaded`() = runTest {
        val exercises = listOf(
            Exercise(name = "Exercise 1", description = "Description 1", routineId = testRoutine.id, index = 0),
            Exercise(name = "Exercise 2", description = "Description 2", routineId = testRoutine.id, index = 1),
            Exercise(name = "Exercise 3", description = "Description 3", routineId = testRoutine.id, index = 2),
        )
        val data = RoutineWithExercises(testRoutine, exercises)
        coEvery { repositoryMock.getRoutineWithExercises(TEST_ROUTINE_ID) } returns data

        viewModel.loadRoutineData(context, TEST_ROUTINE_ID)

        val loadedRoutine = viewModel.routine.value
        assertThat(loadedRoutine).isNotNull
        assertThat(loadedRoutine!!.name).isEqualTo("Some Routine")
        val loadedExercises = viewModel.exercises.value
        assertThat(loadedExercises).isNotNull
        assertThat(loadedExercises).hasSize(3)
    }

    @Test
    fun `addExercise adds to repository and reloads`() = runTest {
        val exercises = mutableListOf<Exercise>()
        val data = RoutineWithExercises(testRoutine, exercises)
        coEvery { repositoryMock.getRoutineWithExercises(TEST_ROUTINE_ID) } returns data
        coEvery { repositoryMock.addExercise(any()) } answers {
            exercises.add(firstArg())
        }

        viewModel.loadRoutineData(context, TEST_ROUTINE_ID)

        var loadedExercises = viewModel.exercises.value
        assertThat(loadedExercises).isEmpty()

        viewModel.addExercise(context, TEST_ROUTINE_ID, "Some Exercise", "some description")

        coVerify { repositoryMock.addExercise(any()) }

        loadedExercises = viewModel.exercises.value
        assertThat(loadedExercises).hasSize(1)

        assertThat(exercises[0].name).isEqualTo("Some Exercise")
        assertThat(exercises[0].description).isEqualTo("some description")
        assertThat(exercises[0].index).isEqualTo(0)
    }

    @Test
    fun `addExercise adds with index on the end`() = runTest {
        val exercises = mutableListOf(
            Exercise(name = "Exercise 1", description = "Description 1", routineId = testRoutine.id, index = 0),
            Exercise(name = "Exercise 2", description = "Description 2", routineId = testRoutine.id, index = 1),
            Exercise(name = "Exercise 3", description = "Description 3", routineId = testRoutine.id, index = 2),
        )
        val data = RoutineWithExercises(testRoutine, exercises)
        coEvery { repositoryMock.getRoutineWithExercises(TEST_ROUTINE_ID) } returns data
        coEvery { repositoryMock.addExercise(any()) } answers {
            exercises.add(firstArg())
        }

        viewModel.loadRoutineData(context, TEST_ROUTINE_ID)

        var loadedExercises = viewModel.exercises.value!!
        assertThat(loadedExercises).hasSize(3)

        viewModel.addExercise(context, TEST_ROUTINE_ID, "Some Exercise", "some description")

        coVerify { repositoryMock.addExercise(any()) }

        loadedExercises = viewModel.exercises.value!!
        assertThat(loadedExercises).hasSize(4)

        assertThat(exercises[3].name).isEqualTo("Some Exercise")
        assertThat(exercises[3].description).isEqualTo("some description")
        assertThat(exercises[3].index).isEqualTo(3)
    }

    @Test
    fun `addExercise trims name and description`() = runTest {
        val exercises = mutableListOf<Exercise>()
        val data = RoutineWithExercises(testRoutine, exercises)
        coEvery { repositoryMock.getRoutineWithExercises(TEST_ROUTINE_ID) } returns data
        coEvery { repositoryMock.addExercise(any()) } answers {
            exercises.add(firstArg())
        }

        viewModel.addExercise(
            context,
            TEST_ROUTINE_ID,
            "\tSome Exercise   ",
            " some description\r\n"
        )

        coVerify { repositoryMock.addExercise(any()) }

        assertThat(exercises[0].name).isEqualTo("Some Exercise")
        assertThat(exercises[0].description).isEqualTo("some description")
    }

    @Test
    fun `deleteExercise deletes from the repository and reloads`() = runTest {
        val exercise1 = Exercise(name = "Exercise 1", description = "Description 1", routineId = testRoutine.id, index = 0)
        val exercises = mutableListOf(
            exercise1,
            Exercise(name = "Exercise 2", description = "Description 2", routineId = testRoutine.id, index = 1),
            Exercise(name = "Exercise 3", description = "Description 3", routineId = testRoutine.id, index = 2),
        )
        val data = RoutineWithExercises(testRoutine, exercises)
        coEvery { repositoryMock.getRoutineWithExercises(TEST_ROUTINE_ID) } returns data
        coEvery { repositoryMock.deleteExercise(any()) } answers {
            exercises.remove(firstArg())
        }

        viewModel.loadRoutineData(context, TEST_ROUTINE_ID)

        var loadedExercises = viewModel.exercises.value!!
        assertThat(loadedExercises).hasSize(3)

        viewModel.deleteExercise(context, TEST_ROUTINE_ID, exercise1)

        coVerify { repositoryMock.deleteExercise(any()) }

        loadedExercises = viewModel.exercises.value!!
        assertThat(loadedExercises).hasSize(2)

        assertThat(exercises[0].name).isEqualTo("Exercise 2")
        assertThat(exercises[1].name).isEqualTo("Exercise 3")
    }

    @Test
    fun `moveExercise moves in loaded exercises`() = runTest {
        val exercises = listOf(
            Exercise(name = "Exercise 1", description = "Description 1", routineId = testRoutine.id, index = 0),
            Exercise(name = "Exercise 2", description = "Description 2", routineId = testRoutine.id, index = 1),
            Exercise(name = "Exercise 3", description = "Description 3", routineId = testRoutine.id, index = 2),
        )
        val data = RoutineWithExercises(testRoutine, exercises)
        coEvery { repositoryMock.getRoutineWithExercises(TEST_ROUTINE_ID) } returns data
        coJustRun { repositoryMock.updateExercises(any()) }

        viewModel.loadRoutineData(context, TEST_ROUTINE_ID)

        var loadedExercises = viewModel.exercises.value!!
        assertThat(loadedExercises).hasSize(3)
        assertThat(loadedExercises[0].name).isEqualTo("Exercise 1")
        assertThat(loadedExercises[0].index).isEqualTo(0)
        assertThat(loadedExercises[1].name).isEqualTo("Exercise 2")
        assertThat(loadedExercises[1].index).isEqualTo(1)
        assertThat(loadedExercises[2].name).isEqualTo("Exercise 3")
        assertThat(loadedExercises[2].index).isEqualTo(2)

        viewModel.moveExercise(2, 0)

        loadedExercises = viewModel.exercises.value!!
        assertThat(loadedExercises).hasSize(3)
        assertThat(loadedExercises[0].name).isEqualTo("Exercise 3")
        assertThat(loadedExercises[0].index).isEqualTo(0)
        assertThat(loadedExercises[1].name).isEqualTo("Exercise 1")
        assertThat(loadedExercises[1].index).isEqualTo(1)
        assertThat(loadedExercises[2].name).isEqualTo("Exercise 2")
        assertThat(loadedExercises[2].index).isEqualTo(2)
    }

    @Test
    fun `moveExercise updates repository for only changed exercises`() = runTest {
        val exercises = listOf(
            Exercise(name = "Exercise 1", description = "Description 1", routineId = testRoutine.id, index = 0),
            Exercise(name = "Exercise 2", description = "Description 2", routineId = testRoutine.id, index = 1),
            Exercise(name = "Exercise 3", description = "Description 3", routineId = testRoutine.id, index = 2),
        )
        val data = RoutineWithExercises(testRoutine, exercises)
        coEvery { repositoryMock.getRoutineWithExercises(TEST_ROUTINE_ID) } returns data
        coJustRun { repositoryMock.updateExercises(any()) }

        viewModel.loadRoutineData(context, TEST_ROUTINE_ID)

        viewModel.moveExercise(2, 1)

        val loadedExercises = viewModel.exercises.value!!
        assertThat(loadedExercises).hasSize(3)
        assertThat(loadedExercises[0].name).isEqualTo("Exercise 1")
        assertThat(loadedExercises[0].index).isEqualTo(0)
        assertThat(loadedExercises[1].name).isEqualTo("Exercise 3")
        assertThat(loadedExercises[1].index).isEqualTo(1)
        assertThat(loadedExercises[2].name).isEqualTo("Exercise 2")
        assertThat(loadedExercises[2].index).isEqualTo(2)

        coVerify {
            repositoryMock.updateExercises(
                withArg { updatedExercises ->
                    assertThat(updatedExercises).hasSize(2)
                    assertThat(updatedExercises[0].name).isEqualTo("Exercise 3")
                    assertThat(updatedExercises[0].index).isEqualTo(1)
                    assertThat(updatedExercises[1].name).isEqualTo("Exercise 2")
                    assertThat(updatedExercises[1].index).isEqualTo(2)
                }
            )
        }
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
