package com.spotgym.spot

import android.content.Context
import com.spotgym.spot.data.Routine
import com.spotgym.spot.data.RoutineRepository
import com.spotgym.spot.home.ExercisesViewModel
import com.spotgym.spot.home.SpotHomeViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
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
class SpotHomeViewModelTest {

    private lateinit var viewModel: SpotHomeViewModel

    @MockK
    private lateinit var context: Context
    @MockK
    private lateinit var repositoryMock: RoutineRepository

    @BeforeEach
    fun beforeEach() {
        every { context.getString(R.string.routine_name) } returns "name"
        every { context.getString(R.string.routine_description) } returns "description"

        val captured = mutableListOf<Any?>()
        every {
            context.getString(eq(R.string.validation_value_empty), *varargAllNullable { captured.add(it) })
        } answers {
            val secondArg = captured[0] as String?
            captured.clear()
            "error $secondArg"
        }

        viewModel = SpotHomeViewModel(repositoryMock)
    }

    @Test
    fun `routines is null initially`() {
        assertThat(viewModel.routines.value).isNull()
    }

    @Test
    fun `routines is loaded`() = runTest {
        val routines = listOf(
            Routine(name = "Routine 1", description = "Description 1", index = 0),
            Routine(name = "Routine 2", description = "Description 2", index = 1),
            Routine(name = "Routine 3", description = "Description 3", index = 2),
        )
        coEvery { repositoryMock.getAllRoutines() } returns routines

        viewModel.loadRoutines()

        val loadedRoutines = viewModel.routines.value
        assertThat(loadedRoutines).isNotNull
        assertThat(loadedRoutines).hasSize(3)
        assertThat(loadedRoutines!![0].name).isEqualTo("Routine 1")
        assertThat(loadedRoutines[1].name).isEqualTo("Routine 2")
        assertThat(loadedRoutines[2].name).isEqualTo("Routine 3")
    }

    @Test
    fun `addRoutine adds to repository and reloads`() = runTest {
        val routines = mutableListOf<Routine>()
        coEvery { repositoryMock.getAllRoutines() } returns routines
        coEvery { repositoryMock.addRoutine(any()) } answers {
            routines.add(firstArg())
        }

        viewModel.loadRoutines()

        val loadedRoutines = viewModel.routines.value
        assertThat(loadedRoutines).isEmpty()

        viewModel.addRoutine("Some Routine", "some description")

        coVerify { repositoryMock.addRoutine(any()) }

        assertThat(loadedRoutines).hasSize(1)

        assertThat(routines[0].name).isEqualTo("Some Routine")
        assertThat(routines[0].description).isEqualTo("some description")
        assertThat(routines[0].index).isEqualTo(0)
    }

    @Test
    fun `addRoutine add with index on the end`() = runTest {
        val routines = mutableListOf(
            Routine(name = "Routine 1", description = "Description 1", index = 0),
            Routine(name = "Routine 2", description = "Description 2", index = 1),
            Routine(name = "Routine 3", description = "Description 3", index = 2),
        )
        coEvery { repositoryMock.getAllRoutines() } returns routines
        coEvery { repositoryMock.addRoutine(any()) } answers {
            routines.add(firstArg())
        }

        viewModel.loadRoutines()

        val loadedRoutines = viewModel.routines.value
        assertThat(loadedRoutines).hasSize(3)

        viewModel.addRoutine("Some Routine", "some description")

        coVerify { repositoryMock.addRoutine(any()) }

        assertThat(loadedRoutines).hasSize(4)

        assertThat(routines[3].name).isEqualTo("Some Routine")
        assertThat(routines[3].description).isEqualTo("some description")
        assertThat(routines[3].index).isEqualTo(3)
    }

    @Test
    fun `addRoutine trims name and description`() = runTest {
        val routines = mutableListOf<Routine>()
        coEvery { repositoryMock.getAllRoutines() } returns routines
        coEvery { repositoryMock.addRoutine(any()) } answers {
            routines.add(firstArg())
        }

        viewModel.addRoutine(
            "\tSome Routine   ",
            " some description\r\n"
        )

        coVerify { repositoryMock.addRoutine(any()) }

        assertThat(routines[0].name).isEqualTo("Some Routine")
        assertThat(routines[0].description).isEqualTo("some description")
    }

    @Test
    fun `deleteRoutine deletes from the repository and reloads`() = runTest {
        val routine1 = Routine(name = "Routine 1", description = "Description 1", index = 0)
        val routines = mutableListOf(
            routine1,
            Routine(name = "Routine 2", description = "Description 2", index = 1),
            Routine(name = "Routine 3", description = "Description 3", index = 2),
        )
        coEvery { repositoryMock.getAllRoutines() } returns routines
        coEvery { repositoryMock.deleteRoutine(any()) } answers {
            routines.remove(firstArg())
        }

        viewModel.loadRoutines()

        val loadedRoutines = viewModel.routines.value
        assertThat(loadedRoutines).hasSize(3)

        viewModel.deleteRoutine(routine1)

        coVerify { repositoryMock.deleteRoutine(routine1) }

        assertThat(loadedRoutines).hasSize(2)

        assertThat(routines[0].name).isEqualTo("Routine 2")
        assertThat(routines[1].name).isEqualTo("Routine 3")
    }

    @ParameterizedTest
    @MethodSource("validationTestData")
    fun `routine validation`(
        name: String,
        description: String,
        expectedResult: Boolean,
        expectedErrorMessage: String?,
        expectedErrorProperty: String?,
    ) {
        val result = viewModel.validateRoutine(
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
        @JvmStatic
        fun validationTestData() = listOf(
            Arguments.of("name", "description", true, null, null),
            Arguments.of("", "description", false, "error name", ExercisesViewModel.EXERCISE_NAME_PROPERTY),
            Arguments.of("name", "", false, "error description", ExercisesViewModel.EXERCISE_DESCRIPTION_PROPERTY),
            Arguments.of("", "", false, "error name", ExercisesViewModel.EXERCISE_NAME_PROPERTY),
        )
    }
}
