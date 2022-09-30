package com.spotgym.spot.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.spotgym.spot.R
import com.spotgym.spot.data.Routine

typealias OnRoutineClicked = (Int) -> Unit

@Composable
@ExperimentalComposeUiApi
fun SpotHome(
    onRoutineClicked: OnRoutineClicked,
    modifier: Modifier = Modifier,
    viewModel: SpotHomeViewModel = hiltViewModel(),
) {
    val routines by viewModel.routines.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadRoutines()
    }

    if (routines == null) {
        SpotLoadingPage(modifier)
    } else {
        val showAddDialog = remember { mutableStateOf(false) }
        if (showAddDialog.value) {
            AddRoutineDialog(
                viewModel = viewModel,
                setShowDialog = { showAddDialog.value = it },
            )
        }

        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = { showAddDialog.value = true }) {
                    Icon(Icons.Filled.Add, stringResource(R.string.routines_add_routine_description))
                }
            },
            scaffoldState = rememberScaffoldState(),
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.routines_title)) },
                )
            },
            modifier = modifier,
        ) { contentPadding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                color = MaterialTheme.colors.background
            ) {
                LazyColumn(modifier = Modifier.padding(10.dp)) {
                    items(routines!!) { routine ->
                        RoutineCard(routine, onRoutineClicked)
                    }
                }
            }
        }
    }
}

@Composable
@ExperimentalComposeUiApi
private fun AddRoutineDialog(
    viewModel: SpotHomeViewModel,
    setShowDialog: SetShowDialog,
) {
    val context = LocalContext.current

    val name = remember { mutableStateOf("") }
    val nameIsError = remember { mutableStateOf(false) }

    val description = remember { mutableStateOf("") }
    val descriptionIsError = remember { mutableStateOf(false) }

    SpotDialog(
        title = stringResource(R.string.routines_add_routine),
        setShowDialog = setShowDialog,
        validate = {
            val result = viewModel.validateRoutine(context, name.value, description.value)
            if (!result.isSuccess) {
                if (result.error!!.property == SpotHomeViewModel.ROUTINE_NAME_PROPERTY) {
                    nameIsError.value = true
                    descriptionIsError.value = false
                } else if (result.error.property == SpotHomeViewModel.ROUTINE_DESCRIPTION_PROPERTY) {
                    nameIsError.value = false
                    descriptionIsError.value = true
                }
            } else {
                nameIsError.value = false
                descriptionIsError.value = false
            }
            result
        },
        onPositiveClick = {
            val routine = Routine(name = name.value, description = description.value)
            viewModel.addRoutine(routine)
        },
        modifier = Modifier
            .padding(35.dp)
            .fillMaxWidth()
            .wrapContentWidth()
    ) {
        DialogValidationTextField(
            label = stringResource(R.string.routine_name),
            value = name.value,
            isError = nameIsError.value,
            onValueChanged = {
                name.value = it
                nameIsError.value = false
            },
            modifier = Modifier.testTag("nameField")
        )

        DialogValidationTextField(
            label = stringResource(R.string.routine_description),
            value = description.value,
            isError = descriptionIsError.value,
            onValueChanged = {
                description.value = it
                descriptionIsError.value = false
            },
            modifier = Modifier.testTag("descField")
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
