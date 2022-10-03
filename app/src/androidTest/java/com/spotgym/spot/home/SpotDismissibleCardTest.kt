package com.spotgym.spot.home

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test

@ExperimentalMaterialApi
class SpotDismissibleCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `displays content text`() {
        setUpCard()

        composeTestRule.onNodeWithText("Some text").assertIsDisplayed().assertHasClickAction()
    }

    @Test
    fun `content is clickable`() {
        var clicked = false
        setUpCard(onCardClicked = { clicked = true })

        composeTestRule.onNodeWithText("Some text").performClick()
        assertThat(clicked).isTrue
    }

    @Test
    fun `when swiped right does nothing`() {
        setUpCard()

        composeTestRule.onNodeWithText("Some text").performTouchInput { swipeRight() }

        composeTestRule.onAllNodesWithText("Delete").assertCountEquals(0)
    }

    @Test
    fun `when swiped left shows delete confirmation dialog`() {
        setUpCard()

        composeTestRule.onNodeWithText("Some text").performTouchInput { swipeLeft() }

        composeTestRule.onNodeWithText("Some title").assertIsDisplayed()
        composeTestRule.onNodeWithText("Some body").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel").assertIsDisplayed().assertHasClickAction()
        composeTestRule.onNodeWithText("Delete").assertIsDisplayed().assertHasClickAction()
    }

    @Test
    fun `when delete is cancelled then no dismiss`() {
        var dismissed = false
        setUpCard(onDismissed = { dismissed = true })

        composeTestRule.onNodeWithText("Some text").performTouchInput { swipeLeft() }

        composeTestRule.onNodeWithText("Cancel").performClick()

        assertThat(dismissed).isFalse
    }

    @Test
    fun `when delete is confirmed then dismiss`() {
        var dismissed = false
        setUpCard(onDismissed = { dismissed = true })

        composeTestRule.onNodeWithText("Some text").performTouchInput { swipeLeft() }

        composeTestRule.onNodeWithText("Delete").performClick()

        assertThat(dismissed).isTrue
    }

    private fun setUpCard(
        onCardClicked: () -> Unit = {},
        onDismissed: () -> Unit = {}
    ) {
        composeTestRule.setContent {
            SpotDismissibleCard(
                onCardClicked = onCardClicked,
                onDismissed = onDismissed,
                confirmTitle = "Some title",
                confirmBody = "Some body"
            ) {
                Text(text = "Some text")
            }
        }
    }
}
