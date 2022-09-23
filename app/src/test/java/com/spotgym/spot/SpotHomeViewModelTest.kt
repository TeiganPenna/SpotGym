package com.spotgym.spot

import android.content.Context
import com.spotgym.spot.data.Routine
import com.spotgym.spot.data.RoutineRepository
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
            val secondArg = captured[0] as String
            captured.clear()
            "some error message $secondArg"
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
            Routine(name = "Routine 1", description = "Description 1"),
            Routine(name = "Routine 2", description = "Description 2"),
            Routine(name = "Routine 3", description = "Description 3"),
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

        val routine = Routine(name = "Some Routine", description = "some description")
        viewModel.addRoutine(routine)

        coVerify { repositoryMock.addRoutine(routine) }

        assertThat(loadedRoutines).hasSize(1)
        assertThat(loadedRoutines!![0]).isEqualTo(routine)
    }

    @Test
    fun `routine validation succeeds for complete routine`() = runTest {
        testValidation(
            name = "name",
            description = "description",
            expectedResult = true,
            expectedErrorMessage = null,
            expectedErrorProperty = null,
        )
    }

    @Test
    fun `routine validation fails when name is empty`() = runTest {
        testValidation(
            name = "",
            description = "description",
            expectedResult = false,
            expectedErrorMessage = "some error message name",
            expectedErrorProperty = "name",
        )
    }

    @Test
    fun `routine validation fails when description is empty`() = runTest {
        testValidation(
            name = "name",
            description = "",
            expectedResult = false,
            expectedErrorMessage = "some error message description",
            expectedErrorProperty = "description",
        )
    }

    @Test
    fun `routine validation fails when routine is empty`() = runTest {
        testValidation(
            name = "",
            description = "",
            expectedResult = false,
            expectedErrorMessage = "some error message name",
            expectedErrorProperty = "name",
        )
    }

    private fun testValidation(
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
}
