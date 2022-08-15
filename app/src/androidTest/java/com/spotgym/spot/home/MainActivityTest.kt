package com.spotgym.spot.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class MainActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun myTest() {
        assertAllRoutinesAreDisplayed()

        composeTestRule.onNodeWithText("Day A").performClick()
//        assertAllRoutinesAreNotDisplayed()
        composeTestRule.onNodeWithText("Bench Press").assertIsDisplayed()
        composeTestRule.onNodeWithText("Paused Bench Press").assertIsDisplayed()
        composeTestRule.onNodeWithText("Incline Dumbbell Press").assertIsDisplayed()
        composeTestRule.onNodeWithText("Chest Cable Flies").assertIsDisplayed()
        composeTestRule.onNodeWithText("Dumbbell Curls").assertIsDisplayed()
        composeTestRule.onNodeWithText("Hammer Curls").assertIsDisplayed()

        composeTestRule.activityRule.scenario.onActivity { activity ->
            activity.onBackPressed()
        }
        assertAllRoutinesAreDisplayed()

        composeTestRule.onNodeWithText("Day B").performClick()
//        assertAllRoutinesAreNotDisplayed()
        composeTestRule.onNodeWithText("Deadlifts").assertIsDisplayed()
        composeTestRule.onNodeWithText("Chin Ups").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cable Rows").assertIsDisplayed()
        composeTestRule.onNodeWithText("Dumbbell Rows").assertIsDisplayed()
        composeTestRule.onNodeWithText("Dips").assertIsDisplayed()
        composeTestRule.onNodeWithText("Leg Press").assertIsDisplayed()
        composeTestRule.onNodeWithText("Calf Raises").assertIsDisplayed()

        composeTestRule.activityRule.scenario.onActivity { activity ->
            activity.onBackPressed()
        }
        assertAllRoutinesAreDisplayed()

        composeTestRule.onNodeWithText("Day C").performClick()
//        assertAllRoutinesAreNotDisplayed()
        composeTestRule.onNodeWithText("Bench Press").assertIsDisplayed()
        composeTestRule.onNodeWithText("Overhead Barbell Press").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tricep Extensions").assertIsDisplayed()
        composeTestRule.onNodeWithText("Overhead Dumbbell Press").assertIsDisplayed()
        composeTestRule.onNodeWithText("Overhead Dumbbell Extensions").assertIsDisplayed()
        composeTestRule.onNodeWithText("Dumbbell Shoulder Flies").assertIsDisplayed()

        composeTestRule.activityRule.scenario.onActivity { activity ->
            activity.onBackPressed()
        }
        assertAllRoutinesAreDisplayed()

        composeTestRule.onNodeWithText("Day D").performClick()
//        assertAllRoutinesAreNotDisplayed()
        composeTestRule.onNodeWithText("Squats").assertIsDisplayed()
        composeTestRule.onNodeWithText("Deadlifts").assertIsDisplayed()
        composeTestRule.onNodeWithText("Leg Press").assertIsDisplayed()
        composeTestRule.onNodeWithText("Calf Raises").assertIsDisplayed()
        composeTestRule.onNodeWithText("Hamstring Curls").assertIsDisplayed()
        composeTestRule.onNodeWithText("Dumbbell Curls").assertIsDisplayed()

        composeTestRule.activityRule.scenario.onActivity { activity ->
            activity.onBackPressed()
        }
        assertAllRoutinesAreDisplayed()
    }

    private fun assertAllRoutinesAreDisplayed() {
        composeTestRule.onNodeWithText("Day A").assertIsDisplayed()
        composeTestRule.onNodeWithText("Day B").assertIsDisplayed()
        composeTestRule.onNodeWithText("Day C").assertIsDisplayed()
        composeTestRule.onNodeWithText("Day D").assertIsDisplayed()
    }

    private fun assertAllRoutinesAreNotDisplayed() {
        composeTestRule.onNodeWithText("Day A").assertIsNotDisplayed()
        composeTestRule.onNodeWithText("Day B").assertIsNotDisplayed()
        composeTestRule.onNodeWithText("Day C").assertIsNotDisplayed()
        composeTestRule.onNodeWithText("Day D").assertIsNotDisplayed()
    }
}
