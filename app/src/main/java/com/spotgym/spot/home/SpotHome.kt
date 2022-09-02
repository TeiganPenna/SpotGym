package com.spotgym.spot.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.spotgym.spot.R
import com.spotgym.spot.data.Routine

typealias OnRoutineClicked = (String) -> Unit

@Composable
fun SpotHome(
    viewModel: MainViewModel,
    onRoutineClicked: OnRoutineClicked,
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {}) {
                Icon(Icons.Filled.Add, stringResource(R.string.routines_add_description))
            }
        },
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

            val routines by viewModel.getRoutines().collectAsState(initial = emptyList())

            LazyColumn(modifier = Modifier.padding(10.dp)) {
                items(routines) { routine ->
                    RoutineCard(routine, onRoutineClicked)
                }
            }
        }
    }
}

@Composable
private fun RoutineCard(
    routine: Routine,
    onRoutineClicked: OnRoutineClicked
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .clickable {
                onRoutineClicked(routine.name) // TODO use id
            },
        elevation = 10.dp
    ) {
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            Text(
                text = routine.name,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = routine.description,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
