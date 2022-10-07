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
import com.spotgym.spot.data.Routine
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

typealias OnRoutineClicked = (Int) -> Unit

@Composable
@ExperimentalComposeUiApi
@ExperimentalMaterialApi
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
        var showAddDialog by remember { mutableStateOf(false) }
        if (showAddDialog) {
            AddRoutineDialog(
                viewModel = viewModel,
                setShowDialog = { showAddDialog = it },
            )
        }

        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = { showAddDialog = true }) {
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
                val state = rememberReorderableLazyListState(onMove = { from, to ->
                    viewModel.moveRoutine(from.index, to.index)
                })

                LazyColumn(
                    state = state.listState,
                    modifier = Modifier
                        .reorderable(state)
                        .detectReorderAfterLongPress(state)
                        .padding(10.dp)
                ) {
                    items(
                        items = routines!!,
                        key = { it.id }
                    ) { routine ->
                        ReorderableItem(
                            reorderableState = state,
                            key = routine.id,
                            modifier = Modifier.padding(5.dp)
                        ) { isDragging ->
                            val elevation = animateDpAsState(if (isDragging) 8.dp else 0.dp)

                            RoutineCard(
                                routine,
                                onRoutineClicked,
                                onDismissed = {
                                    viewModel.deleteRoutine(routine)
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
private fun AddRoutineDialog(
    viewModel: SpotHomeViewModel,
    setShowDialog: SetShowDialog,
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var nameIsError by remember { mutableStateOf(false) }

    var description by remember { mutableStateOf("") }
    var descriptionIsError by remember { mutableStateOf(false) }

    SpotDialog(
        title = stringResource(R.string.routines_add_routine),
        setShowDialog = setShowDialog,
        validate = {
            val result = viewModel.validateRoutine(context, name, description)
            if (!result.isSuccess) {
                if (result.error!!.property == SpotHomeViewModel.ROUTINE_NAME_PROPERTY) {
                    nameIsError = true
                    descriptionIsError = false
                } else if (result.error.property == SpotHomeViewModel.ROUTINE_DESCRIPTION_PROPERTY) {
                    nameIsError = false
                    descriptionIsError = true
                }
            } else {
                nameIsError = false
                descriptionIsError = false
            }
            result
        },
        onPositiveClick = { viewModel.addRoutine(name, description) },
        modifier = Modifier
            .padding(40.dp)
            .fillMaxWidth()
            .wrapContentWidth()
    ) {
        DialogValidationTextField(
            label = stringResource(R.string.routine_name),
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
            label = stringResource(R.string.routine_description),
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
private fun RoutineCard(
    routine: Routine,
    onRoutineClicked: OnRoutineClicked,
    onDismissed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SpotDismissibleCard(
        onCardClicked = { onRoutineClicked(routine.id) },
        onDismissed = onDismissed,
        confirmTitle = stringResource(R.string.routines_dismiss_title, routine.name),
        confirmBody = stringResource(R.string.routines_dismiss_body, routine.name),
        modifier = modifier
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
