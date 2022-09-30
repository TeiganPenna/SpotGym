package com.spotgym.spot.home

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
import com.spotgym.spot.data.Routine
import com.spotgym.spot.data.RoutineRepository
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
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
@ExperimentalComposeUiApi
@ExperimentalCoroutinesApi
class SpotHomeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Mock
    lateinit var routineRepositoryMock: RoutineRepository

    private lateinit var viewModel: SpotHomeViewModel

    @Before
    fun before() {
        viewModel = SpotHomeViewModel(routineRepositoryMock)
    }

    @Test
    fun `shows loading icon when no routines are loaded`() {
        setUpHome(
            onRoutineClicked = {}
        )

        composeTestRule
            .onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo(0.0F, 0.0F..0.0F, 0)))
            .assertIsDisplayed()
    }

    @Test
    fun `when loaded shows title`() = runTest {
        whenever(routineRepositoryMock.getAllRoutines()).thenReturn(emptyList())

        setUpHome(
            onRoutineClicked = {}
        )

        composeTestRule.onNodeWithText("Routines").assertIsDisplayed()
    }

    @Test
    fun `when loaded shows add button`() = runTest {
        whenever(routineRepositoryMock.getAllRoutines()).thenReturn(emptyList())

        setUpHome(
            onRoutineClicked = {}
        )

        composeTestRule
            .onNodeWithContentDescription("Add routine")
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    @Test
    fun `when loads routines should display them`() = runTest {
        whenever(routineRepositoryMock.getAllRoutines()).thenReturn(
            listOf(
                Routine(name = "Foo", description = "Some description"),
                Routine(name = "Bar", description = "Some other description")
            )
        )

        setUpHome(
            onRoutineClicked = {}
        )

        composeTestRule.onNodeWithText("Foo").assertIsDisplayed().assertHasClickAction()
        composeTestRule.onNodeWithText("Some description").assertIsDisplayed().assertHasClickAction()
        composeTestRule.onNodeWithText("Bar").assertIsDisplayed().assertHasClickAction()
        composeTestRule.onNodeWithText("Some other description").assertIsDisplayed().assertHasClickAction()
    }

    @Test
    fun `when loads routine clicked should navigate with id`() = runTest {
        whenever(routineRepositoryMock.getAllRoutines()).thenReturn(
            listOf(
                Routine(id = 4, name = "Foo", description = "Some description"),
            )
        )

        var routineId: Int? = null

        setUpHome(
            onRoutineClicked = { routineId = it }
        )

        composeTestRule.onNodeWithText("Foo").performClick()
        assertThat(routineId).isEqualTo(4)
    }

    @Test
    fun `when adding routine should display dialog`() = runTest {
        whenever(routineRepositoryMock.getAllRoutines()).thenReturn(emptyList())

        setUpHome(
            onRoutineClicked = {}
        )

        composeTestRule.onNodeWithContentDescription("Add routine").performClick()

        composeTestRule.onNodeWithText("Add Routine").assertIsDisplayed()
        composeTestRule.onNodeWithTag("nameField").assertIsDisplayed()
        composeTestRule.onNodeWithTag("descField").assertIsDisplayed()
    }

    @Test
    fun `when cancelled add should do nothing`() = runTest {
        whenever(routineRepositoryMock.getAllRoutines()).thenReturn(emptyList())
        var routine: Routine? = null
        whenever(routineRepositoryMock.addRoutine(any())).doAnswer {
            routine = it.arguments[0] as Routine
        }

        setUpHome(
            onRoutineClicked = {}
        )

        composeTestRule.onNodeWithContentDescription("Add routine").performClick()

        composeTestRule.onNodeWithTag("nameField").performTextInput("Foo")
        composeTestRule.onNodeWithTag("descField").performTextInput("Bar")
        composeTestRule.onNodeWithText("Cancel").performClick()
        assertThat(routine).isNull()
    }

    @Test
    fun `when try to add and no name should show error`() = runTest {
        whenever(routineRepositoryMock.getAllRoutines()).thenReturn(emptyList())
        var routine: Routine? = null
        whenever(routineRepositoryMock.addRoutine(any())).doAnswer {
            routine = it.arguments[0] as Routine
        }

        setUpHome(
            onRoutineClicked = {}
        )

        composeTestRule.onNodeWithContentDescription("Add routine").performClick()

        composeTestRule.onNodeWithTag("nameField").performTextInput("")
        composeTestRule.onNodeWithTag("descField").performTextInput("Bar")
        composeTestRule.onNodeWithText("OK").performClick()

        composeTestRule.onNodeWithText("Name cannot be empty").assertIsDisplayed()
        assertThat(
            getSemanticValueForNodeWithTag(composeTestRule, "nameField", SemanticsProperties.Error)
        ).isEqualTo("Invalid input")
        assertThat(routine).isNull()
    }

    @Test
    fun `when try to add and no desc should show error`() = runTest {
        whenever(routineRepositoryMock.getAllRoutines()).thenReturn(emptyList())
        var routine: Routine? = null
        whenever(routineRepositoryMock.addRoutine(any())).doAnswer {
            routine = it.arguments[0] as Routine
        }

        setUpHome(
            onRoutineClicked = {}
        )

        composeTestRule.onNodeWithContentDescription("Add routine").performClick()

        composeTestRule.onNodeWithTag("nameField").performTextInput("Foo")
        composeTestRule.onNodeWithTag("descField").performTextInput("")
        composeTestRule.onNodeWithText("OK").performClick()

        composeTestRule.onNodeWithText("Description cannot be empty").assertIsDisplayed()
        assertThat(
            getSemanticValueForNodeWithTag(composeTestRule, "descField", SemanticsProperties.Error)
        ).isEqualTo("Invalid input")
        assertThat(routine).isNull()
    }

    @Test
    fun `when try to add and routine is empty should show error`() = runTest {
        whenever(routineRepositoryMock.getAllRoutines()).thenReturn(emptyList())
        var routine: Routine? = null
        whenever(routineRepositoryMock.addRoutine(any())).doAnswer {
            routine = it.arguments[0] as Routine
        }

        setUpHome(
            onRoutineClicked = {}
        )

        composeTestRule.onNodeWithContentDescription("Add routine").performClick()

        composeTestRule.onNodeWithTag("nameField").performTextInput("")
        composeTestRule.onNodeWithTag("descField").performTextInput("")
        composeTestRule.onNodeWithText("OK").performClick()

        composeTestRule.onNodeWithText("Name cannot be empty").assertIsDisplayed()
        assertThat(
            getSemanticValueForNodeWithTag(composeTestRule, "nameField", SemanticsProperties.Error)
        ).isEqualTo("Invalid input")
        assertThat(routine).isNull()
    }

    @Test
    fun `when routine added should display the new routine`() = runTest {
        whenever(routineRepositoryMock.getAllRoutines()).thenReturn(emptyList())
        var routine: Routine? = null
        whenever(routineRepositoryMock.addRoutine(any())).doAnswer {
            routine = it.arguments[0] as Routine
        }

        setUpHome(
            onRoutineClicked = {}
        )

        composeTestRule.onNodeWithContentDescription("Add routine").performClick()

        composeTestRule.onNodeWithTag("nameField").performTextInput("Foo")
        composeTestRule.onNodeWithTag("descField").performTextInput("Bar")
        composeTestRule.onNodeWithText("OK").performClick()

        // can't test the new routine showing without JUnit5 and MainCoroutineExtension
        assertThat(routine!!.name).isEqualTo("Foo")
        assertThat(routine!!.description).isEqualTo("Bar")
    }

    private fun setUpHome(
        onRoutineClicked: OnRoutineClicked,
    ) {
        composeTestRule.setContent {
            SpotHome(
                viewModel = viewModel,
                onRoutineClicked = onRoutineClicked,
            )
        }
    }
}
