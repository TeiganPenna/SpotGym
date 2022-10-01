package com.spotgym.spot.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.spotgym.spot.data.Routine
import com.spotgym.spot.data.RoutineRepositoryImpl
import com.spotgym.spot.data.room.RoutineDao
import com.spotgym.spot.data.room.SpotDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalComposeUiApi
@ExperimentalCoroutinesApi
class SpotHomeTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var db: SpotDatabase
    private lateinit var routineDao: RoutineDao
    private lateinit var viewModel: SpotHomeViewModel

    @Before
    fun before() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(
            context,
            SpotDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        routineDao = db.routineDao()

        val routineRepository = RoutineRepositoryImpl(routineDao)
        viewModel = SpotHomeViewModel(routineRepository)
    }

    @After
    fun after() {
        db.close()
    }

    @Test
    fun `when loaded shows title`(): Unit = runBlocking {
        setUpHome()

        composeTestRule.onNodeWithText("Routines").assertIsDisplayed()
    }

    @Test
    fun `when loaded shows add button`(): Unit = runBlocking {
        setUpHome()

        composeTestRule
            .onNodeWithContentDescription("Add routine")
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    @Test
    fun `when loads routines should display them`(): Unit = runBlocking {
        routineDao.insert(Routine(name = "Foo", description = "Some description"))
        routineDao.insert(Routine(name = "Bar", description = "Some other description"))

        setUpHome()

        composeTestRule.onNodeWithText("Foo").assertIsDisplayed().assertHasClickAction()
        composeTestRule.onNodeWithText("Some description").assertIsDisplayed().assertHasClickAction()
        composeTestRule.onNodeWithText("Bar").assertIsDisplayed().assertHasClickAction()
        composeTestRule.onNodeWithText("Some other description").assertIsDisplayed().assertHasClickAction()
    }

    @Test
    fun `when loads routine clicked should navigate with id`(): Unit = runBlocking {
        routineDao.insert(Routine(id = 4, name = "Foo", description = "Some description"))

        var routineId: Int? = null

        setUpHome(
            onRoutineClicked = { routineId = it }
        )

        composeTestRule.onNodeWithText("Foo").performClick()
        assertThat(routineId).isEqualTo(4)
    }

    @Test
    fun `when adding routine should display dialog`(): Unit = runBlocking {
        setUpHome()

        composeTestRule.onNodeWithContentDescription("Add routine").performClick()

        composeTestRule.onNodeWithText("Add Routine").assertIsDisplayed()
        composeTestRule.onNodeWithTag("nameField").assertIsDisplayed()
        composeTestRule.onNodeWithTag("descField").assertIsDisplayed()
    }

    @Test
    fun `when cancelled add should do nothing`(): Unit = runBlocking {
        setUpHome()

        composeTestRule.onNodeWithContentDescription("Add routine").performClick()

        composeTestRule.onNodeWithTag("nameField").performTextInput("Foo")
        composeTestRule.onNodeWithTag("descField").performTextInput("Bar")
        composeTestRule.onNodeWithText("Cancel").performClick()

        val routines = routineDao.getAll()
        assertThat(routines).isEmpty()
    }

    @Test
    fun `when try to add and no name should show error`(): Unit = runBlocking {
        setUpHome()

        composeTestRule.onNodeWithContentDescription("Add routine").performClick()

        composeTestRule.onNodeWithTag("nameField").performTextInput("")
        composeTestRule.onNodeWithTag("descField").performTextInput("Bar")
        composeTestRule.onNodeWithText("OK").performClick()

        composeTestRule.onNodeWithText("Name cannot be empty").assertIsDisplayed()
        assertThat(
            getSemanticValueForNodeWithTag(composeTestRule, "nameField", SemanticsProperties.Error)
        ).isEqualTo("Invalid input")

        val routines = routineDao.getAll()
        assertThat(routines).isEmpty()
    }

    @Test
    fun `when try to add and no desc should show error`(): Unit = runBlocking {
        setUpHome()

        composeTestRule.onNodeWithContentDescription("Add routine").performClick()

        composeTestRule.onNodeWithTag("nameField").performTextInput("Foo")
        composeTestRule.onNodeWithTag("descField").performTextInput("")
        composeTestRule.onNodeWithText("OK").performClick()

        composeTestRule.onNodeWithText("Description cannot be empty").assertIsDisplayed()
        assertThat(
            getSemanticValueForNodeWithTag(composeTestRule, "descField", SemanticsProperties.Error)
        ).isEqualTo("Invalid input")

        val routines = routineDao.getAll()
        assertThat(routines).isEmpty()
    }

    @Test
    fun `when try to add and routine is empty should show error`(): Unit = runBlocking {
        setUpHome()

        composeTestRule.onNodeWithContentDescription("Add routine").performClick()

        composeTestRule.onNodeWithTag("nameField").performTextInput("")
        composeTestRule.onNodeWithTag("descField").performTextInput("")
        composeTestRule.onNodeWithText("OK").performClick()

        composeTestRule.onNodeWithText("Name cannot be empty").assertIsDisplayed()
        assertThat(
            getSemanticValueForNodeWithTag(composeTestRule, "nameField", SemanticsProperties.Error)
        ).isEqualTo("Invalid input")

        val routines = routineDao.getAll()
        assertThat(routines).isEmpty()
    }

    @Test
    fun `when routine added should display the new routine`(): Unit = runBlocking {
        setUpHome()

        composeTestRule.onNodeWithContentDescription("Add routine").performClick()

        composeTestRule.onNodeWithTag("nameField").performTextInput("Foo")
        composeTestRule.onNodeWithTag("descField").performTextInput("Bar")
        composeTestRule.onNodeWithText("OK").performClick()

        // can't test the new routine showing without JUnit5 and MainCoroutineExtension
        val routines = routineDao.getAll()
        assertThat(routines).hasSize(1)
        assertThat(routines[0].name).isEqualTo("Foo")
        assertThat(routines[0].description).isEqualTo("Bar")
    }

    private fun setUpHome(
        onRoutineClicked: OnRoutineClicked = {},
    ) {
        composeTestRule.setContent {
            SpotHome(
                viewModel = viewModel,
                onRoutineClicked = onRoutineClicked,
            )
        }
    }
}
