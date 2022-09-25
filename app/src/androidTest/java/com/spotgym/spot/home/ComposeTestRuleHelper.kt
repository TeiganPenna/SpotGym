package com.spotgym.spot.home

import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onNodeWithTag

fun <T> getSemanticValueForNodeWithTag(
    composeTestRule: ComposeContentTestRule,
    testTag: String,
    key: SemanticsPropertyKey<T>
): T? {
    return composeTestRule
        .onNodeWithTag(testTag)
        .fetchSemanticsNode().config
        .getOrElseNullable(key) { null }
}
