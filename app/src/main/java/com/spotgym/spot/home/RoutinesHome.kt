package com.spotgym.spot.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.spotgym.spot.R

@Composable
fun RoutinesHome() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.routines_title)) }
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
                RoutineCard("Day A", "Chest day: Bench press, Flies, and Dumbbell curls")
                RoutineCard("Day B", "Back day: Pull ups, Rows, and Leg press")
                RoutineCard("Day C", "Triceps day: Bench press, Extensions, and Flies")
                RoutineCard("Day D", "Leg day: Squats, Deadlifts, and Leg press")
            }
        }
    }
}

@Composable
private fun RoutineCard(name: String, description: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        elevation = 10.dp
//            .clickable {}
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