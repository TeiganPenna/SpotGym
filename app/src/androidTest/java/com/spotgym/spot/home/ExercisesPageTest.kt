package com.spotgym.spot.home

import android.widget.Toast
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.spotgym.spot.data.Exercise
import com.spotgym.spot.data.ExerciseRepository
import com.spotgym.spot.data.Routine
import com.spotgym.spot.data.RoutineWithExercises
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
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
@ExperimentalComposeUiApi
@ExperimentalCoroutinesApi
class ExercisesPageTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Mock
    lateinit var exerciseRepositoryMock: ExerciseRepository
    @Mock
    lateinit var toastServiceMock: ToastService

    private lateinit var viewModel: ExercisesViewModel

    @Before
    fun before() {
        viewModel = ExercisesViewModel(exerciseRepositoryMock, toastServiceMock)
    }

    @Test
    fun `shows loading icon when routine is not loaded`() {
        setUpExercisesPage()

        composeTestRule
            .onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo(0.0F, 0.0F..0.0F, 0)))
            .assertIsDisplayed()
        verify(toastServiceMock).showText(any(), eq("Unable to find routine"), eq(Toast.LENGTH_LONG))
    }

    @Test
    fun `when loaded shows routine name as title`() = runTest {
        whenever(exerciseRepositoryMock.getRoutineWithExercises(TEST_ROUTINE_ID))
            .thenReturn(getRoutineWithExercises(emptyList()))

        setUpExercisesPage()

        composeTestRule.onNodeWithText("My Routine").assertIsDisplayed()
    }

    @Test
    fun `when loaded shows add button`() = runTest {
        whenever(exerciseRepositoryMock.getRoutineWithExercises(TEST_ROUTINE_ID))
            .thenReturn(getRoutineWithExercises(emptyList()))

        setUpExercisesPage()

        composeTestRule
            .onNodeWithContentDescription("Add exercise")
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    @Test
    fun `when loads exercises should display them`() = runTest {
        whenever(exerciseRepositoryMock.getRoutineWithExercises(TEST_ROUTINE_ID))
            .thenReturn(getRoutineWithExercises(listOf(
                Exercise(name = "Foo", description = "Some description", routineId = TEST_ROUTINE_ID),
                Exercise(name = "Bar", description = "Some other description", routineId = TEST_ROUTINE_ID),
            )))

        setUpExercisesPage()

        composeTestRule.onNodeWithText("Foo").assertIsDisplayed()
        composeTestRule.onNodeWithText("Some description").assertIsDisplayed()
        composeTestRule.onNodeWithText("Bar").assertIsDisplayed()
        composeTestRule.onNodeWithText("Some other description").assertIsDisplayed()
    }

    @Test
    fun `when adding exercise should display dialog`() = runTest {
        whenever(exerciseRepositoryMock.getRoutineWithExercises(TEST_ROUTINE_ID))
            .thenReturn(getRoutineWithExercises(emptyList()))

        setUpExercisesPage()

        composeTestRule.onNodeWithContentDescription("Add exercise").performClick()

        composeTestRule.onNodeWithText("Add Exercise").assertIsDisplayed()
        composeTestRule.onNodeWithTag("nameField").assertIsDisplayed()
        composeTestRule.onNodeWithTag("descField").assertIsDisplayed()
    }

    @Test
    fun `when cancelled add should do nothing`() = runTest {
        whenever(exerciseRepositoryMock.getRoutineWithExercises(TEST_ROUTINE_ID))
            .thenReturn(getRoutineWithExercises(emptyList()))
        var exercise: Exercise? = null
        whenever(exerciseRepositoryMock.addExercise(any())).doAnswer {
            exercise = it.arguments[0] as Exercise
        }

        setUpExercisesPage()

        composeTestRule.onNodeWithContentDescription("Add exercise").performClick()

        composeTestRule.onNodeWithTag("nameField").performTextInput("Foo")
        composeTestRule.onNodeWithTag("descField").performTextInput("Bar")
        composeTestRule.onNodeWithText("Cancel").performClick()
        assertThat(exercise).isNull()
    }

    @Test
    fun `when try to add and no name should show error`() = runTest {
        whenever(exerciseRepositoryMock.getRoutineWithExercises(TEST_ROUTINE_ID))
            .thenReturn(getRoutineWithExercises(emptyList()))
        var exercise: Exercise? = null
        whenever(exerciseRepositoryMock.addExercise(any())).doAnswer {
            exercise = it.arguments[0] as Exercise
        }

        setUpExercisesPage()

        composeTestRule.onNodeWithContentDescription("Add exercise").performClick()

        composeTestRule.onNodeWithTag("nameField").performTextInput("")
        composeTestRule.onNodeWithTag("descField").performTextInput("Bar")
        composeTestRule.onNodeWithText("OK").performClick()

        composeTestRule.onNodeWithText("Name cannot be empty").assertIsDisplayed()
        assertThat(
            getSemanticValueForNodeWithTag(composeTestRule, "nameField", SemanticsProperties.Error)
        ).isEqualTo("Invalid input")
        assertThat(exercise).isNull()
    }

    @Test
    fun `when try to add and no desc should show error`() = runTest {
        whenever(exerciseRepositoryMock.getRoutineWithExercises(TEST_ROUTINE_ID))
            .thenReturn(getRoutineWithExercises(emptyList()))
        var exercise: Exercise? = null
        whenever(exerciseRepositoryMock.addExercise(any())).doAnswer {
            exercise = it.arguments[0] as Exercise
        }

        setUpExercisesPage()

        composeTestRule.onNodeWithContentDescription("Add exercise").performClick()

        composeTestRule.onNodeWithTag("nameField").performTextInput("Foo")
        composeTestRule.onNodeWithTag("descField").performTextInput("")
        composeTestRule.onNodeWithText("OK").performClick()

        composeTestRule.onNodeWithText("Description cannot be empty").assertIsDisplayed()
        assertThat(
            getSemanticValueForNodeWithTag(composeTestRule, "descField", SemanticsProperties.Error)
        ).isEqualTo("Invalid input")
        assertThat(exercise).isNull()
    }

    @Test
    fun `when try to add and exercise is empty should show error`() = runTest {
        whenever(exerciseRepositoryMock.getRoutineWithExercises(TEST_ROUTINE_ID))
            .thenReturn(getRoutineWithExercises(emptyList()))
        var exercise: Exercise? = null
        whenever(exerciseRepositoryMock.addExercise(any())).doAnswer {
            exercise = it.arguments[0] as Exercise
        }

        setUpExercisesPage()

        composeTestRule.onNodeWithContentDescription("Add exercise").performClick()

        composeTestRule.onNodeWithTag("nameField").performTextInput("")
        composeTestRule.onNodeWithTag("descField").performTextInput("")
        composeTestRule.onNodeWithText("OK").performClick()

        composeTestRule.onNodeWithText("Name cannot be empty").assertIsDisplayed()
        assertThat(
            getSemanticValueForNodeWithTag(composeTestRule, "nameField", SemanticsProperties.Error)
        ).isEqualTo("Invalid input")
        assertThat(exercise).isNull()
    }

    @Test
    fun `when exercise added should display the new exercise`() = runTest {
        whenever(exerciseRepositoryMock.getRoutineWithExercises(TEST_ROUTINE_ID))
            .thenReturn(getRoutineWithExercises(emptyList()))
        var exercise: Exercise? = null
        whenever(exerciseRepositoryMock.addExercise(any())).doAnswer {
            exercise = it.arguments[0] as Exercise
        }

        setUpExercisesPage()

        composeTestRule.onNodeWithContentDescription("Add exercise").performClick()

        composeTestRule.onNodeWithTag("nameField").performTextInput("Foo")
        composeTestRule.onNodeWithTag("descField").performTextInput("Bar")
        composeTestRule.onNodeWithText("OK").performClick()

        // can't test the new routine showing without JUnit5 and MainCoroutineExtension
        assertThat(exercise!!.routineId).isEqualTo(TEST_ROUTINE_ID)
        assertThat(exercise!!.name).isEqualTo("Foo")
        assertThat(exercise!!.description).isEqualTo("Bar")
    }

    private fun setUpExercisesPage(
        routineId: Int = TEST_ROUTINE_ID,
    ) {
        composeTestRule.setContent {
            ExercisesPage(
                viewModel = viewModel,
                routineId = routineId,
            )
        }
    }

    private fun getRoutineWithExercises(
        exercises: List<Exercise>
    ): RoutineWithExercises {
        return RoutineWithExercises(
            Routine(TEST_ROUTINE_ID, "My Routine", "My routine description"),
            exercises
        )
    }

    companion object {
        const val TEST_ROUTINE_ID = 3
    }
}
