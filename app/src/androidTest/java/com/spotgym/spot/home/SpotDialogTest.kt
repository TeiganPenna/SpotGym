package com.spotgym.spot.home

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test

@ExperimentalComposeUiApi
class SpotDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `dialog title is shown`() {
        setUpDialog(
            setShowDialog = {},
            onPositiveClick = {},
        )
        composeTestRule.onNodeWithText("some title").assertIsDisplayed()
    }

    @Test
    fun `dialog is cancelled does not call positiveClick`() {
        var dialogIsShown = true
        var positiveButtonClicked = false

        setUpDialog(
            setShowDialog = { dialogIsShown = it },
            onPositiveClick = { positiveButtonClicked = true }
        )

        composeTestRule.onNodeWithText("Cancel").performClick()

        assertThat(dialogIsShown).isFalse
        assertThat(positiveButtonClicked).isFalse
    }

    @Test
    fun `when validation passes dialog is closed and positiveClick`() {
        var dialogIsShown = true
        var positiveButtonClicked = false

        setUpDialog(
            setShowDialog = { dialogIsShown = it },
            onPositiveClick = { positiveButtonClicked = true }
        )

        composeTestRule.onNodeWithTag("textField").performTextInput("foo")
        composeTestRule.onNodeWithText("OK").performClick()

        assertThat(dialogIsShown).isFalse
        assertThat(positiveButtonClicked).isTrue
    }

    @Test
    fun `when validation fails dialog is no closed and no positiveClick`() {
        var dialogIsShown = true
        var positiveButtonClicked = false

        setUpDialog(
            setShowDialog = { dialogIsShown = it },
            onPositiveClick = { positiveButtonClicked = true }
        )

        assertThat(
            getSemanticValueForNodeWithTag(composeTestRule, "textField", SemanticsProperties.Error)
        ).isNull() // precondition
        composeTestRule.onNodeWithText("OK").performClick()

        assertThat(dialogIsShown).isTrue
        assertThat(positiveButtonClicked).isFalse
        composeTestRule.onNodeWithText("some error").assertIsDisplayed()
        assertThat(
            getSemanticValueForNodeWithTag(composeTestRule, "textField", SemanticsProperties.Error)
        ).isEqualTo("Invalid input")
    }

    @Test
    fun `DialogValidationTestField has label`() {
        setUpValidationTextField("", false)
        composeTestRule.onNodeWithText("some field").assertIsDisplayed()
    }

    @Test
    fun `DialogValidationTestField shows value`() {
        setUpValidationTextField("some value", false)
        composeTestRule.onNodeWithTag("textField").assertTextEquals("some field", "some value")
    }

    @Test
    fun `DialogValidationTestField is error`() {
        setUpValidationTextField("", true)
        assertThat(
            getSemanticValueForNodeWithTag(composeTestRule, "textField", SemanticsProperties.Error)
        ).isEqualTo("Invalid input")
    }

    @Test
    fun `DialogValidationTestField is not error`() {
        setUpValidationTextField("", false)
        assertThat(getSemanticValueForNodeWithTag(composeTestRule, "textField", SemanticsProperties.Error)).isNull()
    }

    private fun setUpDialog(
        setShowDialog: (Boolean) -> Unit,
        onPositiveClick: () -> Unit,
    ) {
        composeTestRule.setContent {
            val field = remember { mutableStateOf("") }
            val fieldIsError = remember { mutableStateOf(false) }

            SpotDialog(
                title = "some title",
                setShowDialog = setShowDialog,
                validate = {
                    if (field.value.isBlank()) {
                        fieldIsError.value = true
                        ValidationResult.failure("some error", "field")
                    } else {
                        fieldIsError.value = false
                        ValidationResult.success
                    }
                },
                onPositiveClick = onPositiveClick
            ) {
                DialogValidationTextField(
                    label = "some field",
                    value = field,
                    isError = fieldIsError,
                    modifier = Modifier.testTag("textField"),
                )
            }
        }
    }

    private fun setUpValidationTextField(value: String, isError: Boolean) {
        composeTestRule.setContent {
            val mutableValue = remember { mutableStateOf(value) }
            val mutableIsError = remember { mutableStateOf(isError) }
            DialogValidationTextField(
                label = "some field",
                value = mutableValue,
                isError = mutableIsError,
                modifier = Modifier.testTag("textField"),
            )
        }
    }
}
