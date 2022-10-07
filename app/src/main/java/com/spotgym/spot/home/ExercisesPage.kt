package com.spotgym.spot.home

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.spotgym.spot.R
import com.spotgym.spot.data.Exercise
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

@Composable
@ExperimentalComposeUiApi
@ExperimentalMaterialApi
fun ExercisesPage(
    routineId: Int,
    modifier: Modifier = Modifier,
    viewModel: ExercisesViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    LaunchedEffect(routineId) {
        viewModel.loadRoutineData(context, routineId)
    }

    if (viewModel.routine.value == null) {
        SpotLoadingPage(modifier)
    } else {
        val routine = viewModel.routine.value!!

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
                val exercises by viewModel.exercises.collectAsState()

                val state = rememberReorderableLazyListState(onMove = { from, to ->
                    viewModel.moveExercise(from.index, to.index)
                })

                LazyColumn(
                    state = state.listState,
                    modifier = Modifier
                        .reorderable(state)
                        .detectReorderAfterLongPress(state)
                        .padding(10.dp)
                ) {
                    items(
                        items = exercises!!,
                        key = { it.id }
                    ) { exercise ->
                        ReorderableItem(
                            reorderableState = state,
                            key = exercise.id,
                            modifier = Modifier.padding(5.dp)
                        ) { isDragging ->
                            val elevation = animateDpAsState(if (isDragging) 8.dp else 0.dp)

                            ExerciseCard(
                                exercise = exercise,
                                onDismissed = {
                                    viewModel.deleteExercise(context, routineId, exercise)
                                },
                                modifier = Modifier.shadow(elevation.value)
                            )
                        }
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
        onPositiveClick = { viewModel.addExercise(context, routineId, name, description) },
        modifier = Modifier
            .padding(40.dp)
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
            keyboardOptions = KeyboardOptions(KeyboardCapitalization.Words),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("nameField")
        )

        DialogValidationTextField(
            label = stringResource(R.string.exercise_description),
            value = description,
            isError = descriptionIsError,
            onValueChanged = {
                description = it
                descriptionIsError = false
            },
            keyboardOptions = KeyboardOptions(KeyboardCapitalization.Sentences),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("descField")
        )
    }
}

@Composable
@ExperimentalMaterialApi
private fun ExerciseCard(
    exercise: Exercise,
    onDismissed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SpotDismissibleCard(
        onCardClicked = {},
        onDismissed = onDismissed,
        confirmTitle = stringResource(R.string.exercises_dismiss_title, exercise.name),
        confirmBody = stringResource(R.string.exercises_dismiss_body, exercise.name),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            Text(
                text = exercise.name,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = exercise.description,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
