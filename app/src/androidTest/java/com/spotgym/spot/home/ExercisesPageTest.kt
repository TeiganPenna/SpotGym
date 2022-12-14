package com.spotgym.spot.home

import android.widget.Toast
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.spotgym.spot.data.Exercise
import com.spotgym.spot.data.ExerciseRepositoryImpl
import com.spotgym.spot.data.Routine
import com.spotgym.spot.data.room.ExerciseDao
import com.spotgym.spot.data.room.RoutineDao
import com.spotgym.spot.data.room.SpotDatabase
import com.spotgym.spot.ui.service.ToastService
import com.spotgym.spot.ui.theme.SpotTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
@ExperimentalComposeUiApi
@ExperimentalCoroutinesApi
@ExperimentalMaterialApi
class ExercisesPageTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val composeTestRule = createComposeRule()

    @Mock
    lateinit var toastServiceMock: ToastService

    private lateinit var db: SpotDatabase
    private lateinit var exerciseDao: ExerciseDao
    private lateinit var routineDao: RoutineDao
    private lateinit var viewModel: ExercisesViewModel

    private val testRoutine = Routine(TEST_ROUTINE_ID, "My Routine", "My routine description", 0)

    @Before
    fun before() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(
            context,
            SpotDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        exerciseDao = db.exerciseDao()
        routineDao = db.routineDao()

        val exerciseRepository = ExerciseRepositoryImpl(exerciseDao)
        viewModel = ExercisesViewModel(exerciseRepository, toastServiceMock)
    }

    @After
    fun after() {
        db.close()
    }

    @Test
    fun `shows loading icon when routine is not loaded`(): Unit = runBlocking {
        setUpExercisesPage()

        composeTestRule
            .onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo(0.0F, 0.0F..0.0F, 0)))
            .assertIsDisplayed()
        verify(toastServiceMock).showText(any(), eq("Unable to find routine"), eq(Toast.LENGTH_LONG))
    }

    @Test
    fun `when loaded shows routine name as title`(): Unit = runBlocking {
        routineDao.insert(testRoutine)

        setUpExercisesPage()

        composeTestRule.onNodeWithText("My Routine").assertIsDisplayed()
    }

    @Test
    fun `when loaded shows add button`(): Unit = runBlocking {
        routineDao.insert(testRoutine)

        setUpExercisesPage()

        composeTestRule
            .onNodeWithContentDescription("Add exercise")
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    @Test
    fun `when loads exercises should display them`(): Unit = runBlocking {
        routineDao.insert(testRoutine)
        exerciseDao.insert(Exercise(name = "Foo", description = "Some description", routineId = TEST_ROUTINE_ID, index = 0))
        exerciseDao.insert(Exercise(name = "Bar", description = "Some other description", routineId = TEST_ROUTINE_ID, index = 1))

        setUpExercisesPage()

        composeTestRule.onNodeWithText("Foo").assertIsDisplayed().assertHasNoClickAction()
        composeTestRule.onNodeWithText("Some description").assertIsDisplayed().assertHasNoClickAction()
        composeTestRule.onNodeWithText("Bar").assertIsDisplayed().assertHasNoClickAction()
        composeTestRule.onNodeWithText("Some other description").assertIsDisplayed().assertHasNoClickAction()
    }

    @Test
    fun `when adding exercise should display dialog`(): Unit = runBlocking {
        routineDao.insert(testRoutine)

        setUpExercisesPage()

        composeTestRule.onNodeWithContentDescription("Add exercise").performClick()

        composeTestRule.onNodeWithText("Add Exercise").assertIsDisplayed()
        composeTestRule.onNodeWithTag("nameField").assertIsDisplayed()
        composeTestRule.onNodeWithTag("descField").assertIsDisplayed()
    }

    @Test
    fun `when cancelled add should do nothing`(): Unit = runBlocking {
        routineDao.insert(testRoutine)

        setUpExercisesPage()

        composeTestRule.onNodeWithContentDescription("Add exercise").performClick()

        composeTestRule.onNodeWithTag("nameField").performTextInput("Foo")
        composeTestRule.onNodeWithTag("descField").performTextInput("Bar")
        composeTestRule.onNodeWithText("Cancel").performClick()

        val exercises = exerciseDao.getAll()
        assertThat(exercises).isEmpty()
    }

    @Test
    fun `when try to add and no name should show error`(): Unit = runBlocking {
        routineDao.insert(testRoutine)

        setUpExercisesPage()

        composeTestRule.onNodeWithContentDescription("Add exercise").performClick()

        composeTestRule.onNodeWithTag("nameField").performTextInput("")
        composeTestRule.onNodeWithTag("descField").performTextInput("Bar")
        composeTestRule.onNodeWithText("OK").performClick()

        composeTestRule.onNodeWithText("Name cannot be empty").assertIsDisplayed()
        assertThat(
            getSemanticValueForNodeWithTag(composeTestRule, "nameField", SemanticsProperties.Error)
        ).isEqualTo("Invalid input")

        val exercises = exerciseDao.getAll()
        assertThat(exercises).isEmpty()
    }

    @Test
    fun `when try to add and no desc should show error`(): Unit = runBlocking {
        routineDao.insert(testRoutine)

        setUpExercisesPage()

        composeTestRule.onNodeWithContentDescription("Add exercise").performClick()

        composeTestRule.onNodeWithTag("nameField").performTextInput("Foo")
        composeTestRule.onNodeWithTag("descField").performTextInput("")
        composeTestRule.onNodeWithText("OK").performClick()

        composeTestRule.onNodeWithText("Description cannot be empty").assertIsDisplayed()
        assertThat(
            getSemanticValueForNodeWithTag(composeTestRule, "descField", SemanticsProperties.Error)
        ).isEqualTo("Invalid input")

        val exercises = exerciseDao.getAll()
        assertThat(exercises).isEmpty()
    }

    @Test
    fun `when try to add and exercise is empty should show error`(): Unit = runBlocking {
        routineDao.insert(testRoutine)

        setUpExercisesPage()

        composeTestRule.onNodeWithContentDescription("Add exercise").performClick()

        composeTestRule.onNodeWithTag("nameField").performTextInput("")
        composeTestRule.onNodeWithTag("descField").performTextInput("")
        composeTestRule.onNodeWithText("OK").performClick()

        composeTestRule.onNodeWithText("Name cannot be empty").assertIsDisplayed()
        assertThat(
            getSemanticValueForNodeWithTag(composeTestRule, "nameField", SemanticsProperties.Error)
        ).isEqualTo("Invalid input")

        val exercises = exerciseDao.getAll()
        assertThat(exercises).isEmpty()
    }

    @Test
    fun `when exercise added should display the new exercise`(): Unit = runBlocking {
        routineDao.insert(testRoutine)

        setUpExercisesPage()

        composeTestRule.onNodeWithContentDescription("Add exercise").performClick()

        composeTestRule.onNodeWithTag("nameField").performTextInput("Foo")
        composeTestRule.onNodeWithTag("descField").performTextInput("Bar")
        composeTestRule.onNodeWithText("OK").performClick()

        val exercises = exerciseDao.getAll()
        assertThat(exercises).hasSize(1)
        assertThat(exercises[0].routineId).isEqualTo(TEST_ROUTINE_ID)
        assertThat(exercises[0].name).isEqualTo("Foo")
        assertThat(exercises[0].description).isEqualTo("Bar")

        composeTestRule.onNodeWithText("Foo").assertIsDisplayed().assertHasNoClickAction()
        composeTestRule.onNodeWithText("Bar").assertIsDisplayed().assertHasNoClickAction()
    }

    @Test
    fun `when exercise added should trim name and description`(): Unit = runBlocking {
        routineDao.insert(testRoutine)

        setUpExercisesPage()

        composeTestRule.onNodeWithContentDescription("Add exercise").performClick()

        composeTestRule.onNodeWithTag("nameField").performTextInput("\tFoo   ")
        composeTestRule.onNodeWithTag("descField").performTextInput(" Bar\r\n")
        composeTestRule.onNodeWithText("OK").performClick()

        val exercises = exerciseDao.getAll()
        assertThat(exercises).hasSize(1)
        assertThat(exercises[0].routineId).isEqualTo(TEST_ROUTINE_ID)
        assertThat(exercises[0].name).isEqualTo("Foo")
        assertThat(exercises[0].description).isEqualTo("Bar")
    }

    @Test
    fun `when exercise swiped should display dialog`(): Unit = runBlocking {
        routineDao.insert(testRoutine)
        exerciseDao.insert(Exercise(name = "Foo", description = "Some description", routineId = TEST_ROUTINE_ID, index = 0))

        setUpExercisesPage()

        composeTestRule.onNodeWithText("Foo").performTouchInput { swipeLeft() }

        composeTestRule.onNodeWithText("Delete 'Foo'?").assertIsDisplayed()
        composeTestRule.onNodeWithText("'Foo' exercise will be deleted forever.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel").assertIsDisplayed().assertHasClickAction()
        composeTestRule.onNodeWithText("Delete").assertIsDisplayed().assertHasClickAction()
    }

    @Test
    fun `when delete cancelled should do nothing`(): Unit = runBlocking {
        routineDao.insert(testRoutine)
        exerciseDao.insert(Exercise(name = "Foo", description = "Some description", routineId = TEST_ROUTINE_ID, index = 0))

        setUpExercisesPage()

        composeTestRule.onNodeWithText("Foo").performTouchInput { swipeLeft() }

        composeTestRule.onNodeWithText("Cancel").performClick()

        composeTestRule.onNodeWithText("Foo").assertIsDisplayed().assertHasNoClickAction()
        val exercises = exerciseDao.getAll()
        assertThat(exercises).hasSize(1)
        assertThat(exercises[0].name).isEqualTo("Foo")
        assertThat(exercises[0].description).isEqualTo("Some description")
    }

    @Test
    fun `when exercise deleted should not be in the repository`(): Unit = runBlocking {
        routineDao.insert(testRoutine)
        exerciseDao.insert(Exercise(name = "Foo", description = "Some description", routineId = TEST_ROUTINE_ID, index = 0))

        setUpExercisesPage()

        composeTestRule.onNodeWithText("Foo").performTouchInput { swipeLeft() }

        composeTestRule.onNodeWithText("Delete").performClick()

        composeTestRule.onAllNodesWithText("Foo").assertCountEquals(0)
        val exercises = exerciseDao.getAll()
        assertThat(exercises).isEmpty()
    }

    private fun setUpExercisesPage() {
        composeTestRule.setContent {
            SpotTheme {
                ExercisesPage(
                    viewModel = viewModel,
                    routineId = TEST_ROUTINE_ID,
                )
            }
        }
    }

    companion object {
        const val TEST_ROUTINE_ID = 3
    }
}
