package com.spotgym.spot.home

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

class SpotLoadingPageTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `shows loading animation`() {
        composeTestRule.setContent {
            SpotLoadingPage()
        }

        composeTestRule
            .onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo(0.0F, 0.0F..0.0F, 0)))
            .assertIsDisplayed()
    }
}
