package com.spotgym.spot

import android.content.Context
import androidx.compose.runtime.mutableStateOf
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
    fun `validateRoutine succeeds for complete routine`() = runTest {
        val nameIsError = mutableStateOf(false)
        val descriptionIsError = mutableStateOf(false)
        val result = viewModel.validateRoutine(
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
    fun `validateRoutine fails when name is empty`() = runTest {
        val nameIsError = mutableStateOf(false)
        val descriptionIsError = mutableStateOf(false)
        val result = viewModel.validateRoutine(
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
    fun `validateRoutine fails when description is empty`() = runTest {
        val nameIsError = mutableStateOf(false)
        val descriptionIsError = mutableStateOf(false)
        val result = viewModel.validateRoutine(
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
    fun `validateRoutine resets nameIsError when name is ok`() = runTest {
        val nameIsError = mutableStateOf(true)
        val descriptionIsError = mutableStateOf(false)
        val result = viewModel.validateRoutine(
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
    fun `validateRoutine resets descriptionIsError when description is ok`() = runTest {
        val nameIsError = mutableStateOf(false)
        val descriptionIsError = mutableStateOf(true)
        val result = viewModel.validateRoutine(
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
    fun `validateRoutine resets descriptionIsError when name is empty`() = runTest {
        val nameIsError = mutableStateOf(false)
        val descriptionIsError = mutableStateOf(true)
        val result = viewModel.validateRoutine(
            context,
            mutableStateOf(""),
            nameIsError,
            mutableStateOf(""),
            descriptionIsError,
        )

        assertThat(result.isSuccess).isFalse
        assertThat(result.message).isEqualTo("some error message name")
        assertThat(nameIsError.value).isTrue
        assertThat(descriptionIsError.value).isFalse
    }
}
