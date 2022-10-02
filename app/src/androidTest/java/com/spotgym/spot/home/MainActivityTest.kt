package com.spotgym.spot.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class MainActivityTest {

    @get:Rule(order = 1)
    val hiltTestRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule(order = 3)
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun before() {
        hiltTestRule.inject()
    }

    @Test
    fun `create and store some routines with exercises`(): Unit = runBlocking {
        // add leg day routine
        addRoutine("Leg Day", "Squats and Deadlifts")

        // check leg day routine exists
        composeTestRule.onNodeWithText("Leg Day").assertIsDisplayed()

        // open leg day routine
        composeTestRule.onNodeWithText("Leg Day").performClick()

        // add leg day exercises
        addExercise("Squats", "5 sets of 5 reps")
        addExercise("Deadlifts", "3 sets of 8-12 reps")

        // check leg day exercises exist
        composeTestRule.onNodeWithText("Squats").assertIsDisplayed()
        composeTestRule.onNodeWithText("Deadlifts").assertIsDisplayed()

        // go back to routines page
        composeTestRule.activityRule.scenario.onActivity { activity ->
            activity.onBackPressed()
        }

        // check leg day routine is still there
        composeTestRule.onNodeWithText("Leg Day").assertIsDisplayed()

        // add chest day routine
        addRoutine("Chest Day", "Bench press")

        // check both routines exist
        composeTestRule.onNodeWithText("Leg Day").assertIsDisplayed()
        composeTestRule.onNodeWithText("Chest Day").assertIsDisplayed()

        // open chest day routine
        composeTestRule.onNodeWithText("Chest Day").performClick()

        // add chest day exercises
        addExercise("Bench Press", "5 sets of 5 reps")

        // check chest day exercises exist
        composeTestRule.onNodeWithText("Bench Press").assertIsDisplayed()

        // go back to routines page
        composeTestRule.activityRule.scenario.onActivity { activity ->
            activity.onBackPressed()
        }

        // open leg day routine
        composeTestRule.onNodeWithText("Leg Day").performClick()

        // check leg day exercises are still there
        composeTestRule.onNodeWithText("Squats").assertIsDisplayed()
        composeTestRule.onNodeWithText("Deadlifts").assertIsDisplayed()
    }

    @Test
    fun `create and delete a routine with exercises`(): Unit = runBlocking {
        // add leg day routine
        addRoutine("Leg Day", "Squats and Deadlifts")

        // open leg day routine
        composeTestRule.onNodeWithText("Leg Day").performClick()

        // add leg day exercises
        addExercise("Squats", "5 sets of 5 reps")
        addExercise("Deadlifts", "3 sets of 8-12 reps")

        // go back to routines page
        composeTestRule.activityRule.scenario.onActivity { activity ->
            activity.onBackPressed()
        }

        composeTestRule.onNodeWithText("Leg Day").performTouchInput { swipeLeft() }
        composeTestRule.onNodeWithText("Delete routine?").assertIsDisplayed()
        composeTestRule.onNodeWithText("Delete").performClick()

        composeTestRule.onAllNodesWithText("Leg Day").assertCountEquals(0)
    }

    private fun addRoutine(name: String, description: String) {
        composeTestRule.onNodeWithContentDescription("Add routine").performClick()
        composeTestRule.onNodeWithTag("nameField").performTextInput(name)
        composeTestRule.onNodeWithTag("descField").performTextInput(description)
        composeTestRule.onNodeWithText("OK").performClick()
    }

    private fun addExercise(name: String, description: String) {
        composeTestRule.onNodeWithContentDescription("Add exercise").performClick()
        composeTestRule.onNodeWithTag("nameField").performTextInput(name)
        composeTestRule.onNodeWithTag("descField").performTextInput(description)
        composeTestRule.onNodeWithText("OK").performClick()
    }
}
