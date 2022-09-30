package com.spotgym.spot.home

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.spotgym.spot.data.Exercise

@Composable
@ExperimentalComposeUiApi
fun ExercisesPage(
    routineId: Int,
    modifier: Modifier = Modifier,
    viewModel: ExercisesViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    LaunchedEffect(routineId) {
        viewModel.loadRoutineData(context, routineId)
    }

    if (viewModel.routineData == null) {
        SpotLoadingPage(modifier)
    } else {
        val routine = viewModel.routineData!!.routine

        var showAddDialog by remember { mutableStateOf(false) }
        if (showAddDialog) {
            AddExerciseDialog(
                viewModel = viewModel,
                routineId = routine.id,
                setShowDialog = { showAddDialog = it }
            )
        }

        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = { showAddDialog = true }) {
                    Icon(Icons.Filled.Add, stringResource(R.string.exercises_add_exercise_description))
                }
            },
            scaffoldState = rememberScaffoldState(),
            topBar = {
                TopAppBar(
                    title = { Text(routine.name) },
                )
            },
            modifier = modifier
        ) { contentPadding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                color = MaterialTheme.colors.background
            ) {
                LazyColumn(modifier = Modifier.padding(10.dp)) {
                    val exercises = viewModel.routineData!!.exercises

                    items(exercises) { exercise ->
                        ExerciseCard(
                            name = exercise.name,
                            description = exercise.description
                        )
                    }
                }
            }
        }
    }
}

@Composable
@ExperimentalComposeUiApi
private fun AddExerciseDialog(
    viewModel: ExercisesViewModel,
    routineId: Int,
    setShowDialog: SetShowDialog,
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var nameIsError by remember { mutableStateOf(false) }

    var description by remember { mutableStateOf("") }
    var descriptionIsError by remember { mutableStateOf(false) }

    SpotDialog(
        title = stringResource(R.string.exercises_add_exercise),
        setShowDialog = setShowDialog,
        validate = {
            val result = viewModel.validateExercise(context, name, description)
            if (!result.isSuccess) {
                if (result.error!!.property == ExercisesViewModel.EXERCISE_NAME_PROPERTY) {
                    nameIsError = true
                    descriptionIsError = false
                } else if (result.error.property == ExercisesViewModel.EXERCISE_DESCRIPTION_PROPERTY) {
                    nameIsError = false
                    descriptionIsError = true
                }
            } else {
                nameIsError = false
                descriptionIsError = false
            }
            result
        },
        onPositiveClick = {
            val exercise = Exercise(name = name, description = description, routineId = routineId)
            viewModel.addExercise(context, routineId, exercise)
        },
        modifier = Modifier
            .padding(35.dp)
            .fillMaxWidth()
            .wrapContentWidth()
    ) {
        DialogValidationTextField(
            label = stringResource(R.string.exercise_name),
            value = name,
            isError = nameIsError,
            onValueChanged = {
                name = it
                nameIsError = false
            },
            modifier = Modifier.testTag("nameField")
        )

        DialogValidationTextField(
            label = stringResource(R.string.exercise_description),
            value = description,
            isError = descriptionIsError,
            onValueChanged = {
                description = it
                descriptionIsError = false
            },
            modifier = Modifier.testTag("descField")
        )
    }
}

@Composable
private fun ExerciseCard(
    name: String,
    description: String,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
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
