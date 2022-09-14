package com.spotgym.spot.home

import android.content.Context
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.spotgym.spot.R
import com.spotgym.spot.data.Routine
import kotlinx.coroutines.launch

typealias OnRoutineClicked = (Int) -> Unit

@Composable
@ExperimentalComposeUiApi
fun SpotHome(
    viewModel: SpotHomeViewModel = hiltViewModel(),
    onRoutineClicked: OnRoutineClicked,
) {
    val coroutineScope = rememberCoroutineScope()
    val addRoutine: (Routine) -> Unit = {
        coroutineScope.launch {
            viewModel.addRoutine(it)
            viewModel.refreshRoutines()
        }
    }

    val showAddDialog = remember { mutableStateOf(false) }
    if (showAddDialog.value) {
        AddRoutineDialog(
            showDialog = showAddDialog,
            onPositiveClick = { addRoutine(it) }
        )
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
            val routines by viewModel.routines.collectAsState(initial = emptyList())

            LazyColumn(modifier = Modifier.padding(10.dp)) {
                items(routines) { routine ->
                    RoutineCard(routine, onRoutineClicked)
                }
            }
        }
    }
}

@Composable
@ExperimentalComposeUiApi
private fun AddRoutineDialog(
    showDialog: MutableState<Boolean>,
    onPositiveClick: (Routine) -> Unit,
) {
    val context = LocalContext.current

    val name = remember { mutableStateOf("") }
    val nameIsError = remember { mutableStateOf(false) }

    val description = remember { mutableStateOf("") }
    val descriptionIsError = remember { mutableStateOf(false) }

    SpotDialog(
        title = stringResource(R.string.routines_name_routine),
        setShowDialog = { showDialog.value = it },
        validate = { validateRoutine(context, name, nameIsError, description, descriptionIsError) },
        onPositiveClick = {
            val routine = Routine(name = name.value, description = description.value)
            onPositiveClick(routine)
        },
    ) {
        DialogValidationTextField(
            label = stringResource(R.string.routine_name),
            value = name,
            isError = nameIsError
        )

        DialogValidationTextField(
            label = stringResource(R.string.routine_description),
            value = description,
            isError = descriptionIsError
        )
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
                onRoutineClicked(routine.id)
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

@SuppressWarnings("ReturnCount")
private fun validateRoutine(
    context: Context,
    name: MutableState<String>,
    nameIsError: MutableState<Boolean>,
    description: MutableState<String>,
    descriptionIsError: MutableState<Boolean>,
): ValidationResult {
    if (name.value.isBlank()) {
        nameIsError.value = true
        return ValidationResult(
            false,
            context.getString(
                R.string.routines_validation_empty,
                context.getString(R.string.routine_name)
            )
        )
    } else {
        nameIsError.value = false
    }
    if (description.value.isBlank()) {
        descriptionIsError.value = true
        return ValidationResult(
            false,
            context.getString(
                R.string.routines_validation_empty,
                context.getString(R.string.routine_description)
            )
        )
    } else {
        descriptionIsError.value = false
    }
    return ValidationResult(true, null)
}
