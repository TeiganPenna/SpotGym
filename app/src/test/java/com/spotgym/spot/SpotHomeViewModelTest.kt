package com.spotgym.spot

import android.content.Context
import com.spotgym.spot.data.Routine
import com.spotgym.spot.data.RoutineRepository
import com.spotgym.spot.home.SpotHomeViewModel
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
class SpotHomeViewModelTest {

    private lateinit var viewModel: SpotHomeViewModel

    @Mock
    private lateinit var context: Context
    @Mock
    private lateinit var repositoryMock: RoutineRepository

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun before() {
        whenever(context.getString(R.string.routine_name)).thenReturn("name")
        whenever(context.getString(R.string.routine_description)).thenReturn("description")
        whenever(context.getString(eq(R.string.validation_value_empty), any())).then {
            "some error message " + it.arguments[1]
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
        whenever(repositoryMock.getAllRoutines()).thenReturn(routines)

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
        whenever(repositoryMock.getAllRoutines()).thenReturn(routines)
        whenever(repositoryMock.addRoutine(any())).then {
            routines.add(it.arguments[0] as Routine)
        }

        viewModel.loadRoutines()

        val loadedRoutines = viewModel.routines.value
        assertThat(loadedRoutines).isEmpty()

        val routine = Routine(name = "Some Routine", description = "some description")
        viewModel.addRoutine(routine)

        verify(repositoryMock).addRoutine(routine)

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
