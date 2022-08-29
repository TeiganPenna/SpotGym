package com.spotgym.spot.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.spotgym.spot.R

typealias OnRoutineClicked = (String) -> Unit

@Composable
fun SpotHome(
    onRoutineClicked: OnRoutineClicked
) {
    Scaffold(
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.routines_title)) },
            )
        }
    ) { contentPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            color = MaterialTheme.colors.background
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                RoutineCard(
                    name = "Day A",
                    description = "Chest day: Bench press, Flies, and Dumbbell curls",
                    onRoutineClicked = onRoutineClicked
                )
                RoutineCard(
                    name = "Day B", description = "Back day: Pull ups, Rows, and Leg press",
                    onRoutineClicked = onRoutineClicked
                )
                RoutineCard(
                    name = "Day C",
                    description = "Triceps day: Bench press, Extensions, and Flies",
                    onRoutineClicked = onRoutineClicked
                )
                RoutineCard(
                    name = "Day D",
                    description = "Leg day: Squats, Deadlifts, and Leg press",
                    onRoutineClicked = onRoutineClicked
                )
            }
        }
    }
}

@Composable
private fun RoutineCard(
    name: String,
    description: String,
    onRoutineClicked: OnRoutineClicked
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .clickable {
                onRoutineClicked(name)
            },
        elevation = 10.dp
    ) {
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            Text(
                text = name,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
