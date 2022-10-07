package com.spotgym.spot.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.spotgym.spot.R

@Composable
@ExperimentalMaterialApi
fun SpotDismissibleCard(
    onCardClicked: (() -> Unit)?,
    onDismissed: () -> Unit,
    confirmTitle: String,
    confirmBody: String,
    modifier: Modifier = Modifier,
    content: @Composable
    () -> Unit,
) {
    var showDismissAlert by remember { mutableStateOf(false) }
    if (showDismissAlert) {
        DismissCardAlert(
            setShowAlert = { showDismissAlert = it },
            onConfirmed = onDismissed,
            confirmTitle = confirmTitle,
            confirmBody = confirmBody,
        )
    }

    val dismissState = rememberDismissState(
        confirmStateChange = {
            if (it == DismissValue.DismissedToStart) showDismissAlert = true
            false // always disable, this is the only way to have an effective confirmation dialog
        }
    )

    SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.EndToStart),
        modifier = modifier,
        background = {
            Box(
                contentAlignment = Alignment.CenterEnd,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = stringResource(R.string.spot_card_delete)
                )
            }
        }
    ) {
        SpotCard(onCardClicked) {
            content()
        }
    }
}

@Composable
private fun DismissCardAlert(
    setShowAlert: (Boolean) -> Unit,
    onConfirmed: () -> Unit,
    confirmTitle: String,
    confirmBody: String,
) {
    AlertDialog(
        onDismissRequest = { setShowAlert(false) },
        title = {
            Text(text = confirmTitle)
        },
        text = {
            Text(text = confirmBody)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    setShowAlert(false)
                    onConfirmed()
                }
            ) {
                Text(text = stringResource(R.string.delete))
            }
        },
        dismissButton = {
            TextButton(onClick = { setShowAlert(false) }) {
                Text(text = stringResource(android.R.string.cancel))
            }
        }
    )
}

@Composable
private fun SpotCard(
    onCardClicked: (() -> Unit)?,
    content: @Composable
    () -> Unit
) {
    if (onCardClicked != null) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onCardClicked()
                },
            elevation = 10.dp
        ) {
            content()
        }
    } else {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = 10.dp
        ) {
            content()
        }
    }
}
