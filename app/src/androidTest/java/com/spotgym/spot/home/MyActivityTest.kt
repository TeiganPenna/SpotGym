package com.spotgym.spot.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class MyActivityTest {

    @get:Rule(order = 1)
    val hiltTestRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun before() {
        hiltTestRule.inject()
    }

    @Test
    fun `create and store some routines with exercises`() {
        // add leg day routine
        composeTestRule.onNodeWithContentDescription("Add routine").performClick()
        composeTestRule.onNodeWithTag("nameField").performTextInput("Leg Day")
        composeTestRule.onNodeWithTag("descField").performTextInput("Squats and Deadlifts")
        composeTestRule.onNodeWithText("OK").performClick()

        // check leg day routine exists
        composeTestRule.onNodeWithText("Leg Day").assertIsDisplayed()

        // open leg day routine
        composeTestRule.onNodeWithText("Leg Day").performClick()

        // add leg day exercises
        composeTestRule.onNodeWithContentDescription("Add exercise").performClick()
        composeTestRule.onNodeWithTag("nameField").performTextInput("Squats")
        composeTestRule.onNodeWithTag("descField").performTextInput("5 sets of 5 reps")
        composeTestRule.onNodeWithText("OK").performClick()
        composeTestRule.onNodeWithContentDescription("Add exercise").performClick()
        composeTestRule.onNodeWithTag("nameField").performTextInput("Deadlifts")
        composeTestRule.onNodeWithTag("descField").performTextInput("3 sets of 8-12 reps")
        composeTestRule.onNodeWithText("OK").performClick()

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
        composeTestRule.onNodeWithContentDescription("Add routine").performClick()
        composeTestRule.onNodeWithTag("nameField").performTextInput("Chest Day")
        composeTestRule.onNodeWithTag("descField").performTextInput("Bench press")
        composeTestRule.onNodeWithText("OK").performClick()

        // check both routines exist
        composeTestRule.onNodeWithText("Leg Day").assertIsDisplayed()
        composeTestRule.onNodeWithText("Chest Day").assertIsDisplayed()

        // open chest day routine
        composeTestRule.onNodeWithText("Chest Day").performClick()

        // add chest day exercises
        composeTestRule.onNodeWithContentDescription("Add exercise").performClick()
        composeTestRule.onNodeWithTag("nameField").performTextInput("Bench Press")
        composeTestRule.onNodeWithTag("descField").performTextInput("5 sets of 5 reps")
        composeTestRule.onNodeWithText("OK").performClick()

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
}
