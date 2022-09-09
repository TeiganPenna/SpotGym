package com.spotgym.spot.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.spotgym.spot.R
import com.spotgym.spot.data.Routine
import kotlinx.coroutines.launch

typealias OnRoutineClicked = (String) -> Unit

@Composable
@ExperimentalComposeUiApi
fun SpotHome(
    viewModel: MainViewModel,
    onRoutineClicked: OnRoutineClicked,
) {
    val coroutineScope = rememberCoroutineScope()
    val addRoutine: (Routine) -> Unit = {
        coroutineScope.launch {
            viewModel.addRoutine(it)
        }
    }

    val showAddDialog = remember { mutableStateOf(false) }
    if (showAddDialog.value) {
        AddRoutineDialog(setShowDialog = { showAddDialog.value = it }) {
            addRoutine(it)
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog.value = true }) {
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

@Composable
@ExperimentalComposeUiApi
private fun AddRoutineDialog(
    setShowDialog: (Boolean) -> Unit,
    onPositiveClick: (Routine) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var errorField by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = { setShowDialog(false) },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            elevation = 10.dp,
            modifier = Modifier
                .padding(35.dp)
                .fillMaxWidth()
                .wrapContentWidth()
        ) {
            Column(
                modifier = Modifier.padding(15.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = stringResource(R.string.routines_name_routine),
                    fontWeight = FontWeight.Bold
                )

                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.routine_field_name)) }
//                    modifier = Modifier.border(
//                        BorderStroke(
//                            width = 2.dp,
//                            color = colorResource(id = if (errorField.isEmpty()) R.color.error_red else R.color.black)
//                        )
//                    ) // TODO make border red when error
                )

                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.routine_field_desc)) }
                )

                if (errorField.isNotBlank()) {
                    Text(text = errorField, color = colorResource(R.color.error_red))
                }

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = { setShowDialog(false) }) {
                        Text(text = stringResource(R.string.dialog_button_cancel))
                    }
                    Spacer(modifier = Modifier.width(3.dp))
                    TextButton(onClick = {
                        if (name.isBlank()) {
                            errorField = "Name cannot be empty"
                            return@TextButton
                        }
                        if (description.isBlank()) {
                            errorField = "Description cannot be empty"
                            return@TextButton
                        }
                        onPositiveClick(Routine(name = name, description = description))
                        setShowDialog(false)
                    }) {
                        Text(text = stringResource(R.string.dialog_button_ok))
                    }
                }
            }
        }
    }
}
