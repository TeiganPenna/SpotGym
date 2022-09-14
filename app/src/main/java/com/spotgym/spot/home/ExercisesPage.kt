package com.spotgym.spot.home

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.spotgym.spot.R
import com.spotgym.spot.data.Routine

@Composable
fun ExercisesPage(
    viewModel: MainViewModel,
    routineId: Int
) {
    val context = LocalContext.current
    var routine by remember { mutableStateOf<Routine?>(null) }

    LaunchedEffect(routineId) {
        routine = getRoutine(context, viewModel, routineId)
    }

    Scaffold(
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopAppBar(
                title = { Text(routine?.name ?: "") },
            )
        }
    ) { contentPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            color = MaterialTheme.colors.background
        ) {
            if (routine == null) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CircularProgressIndicator()
                }
            } else {
                when (routine!!.name) {
                    "Day A" -> {
                        Column(modifier = Modifier.padding(10.dp)) {
                            ExerciseCard(name = "Bench Press", description = "3 sets of 5 reps")
                            ExerciseCard(name = "Paused Bench Press", description = "4 sets of 3 reps")
                            ExerciseCard(
                                name = "Incline Dumbbell Press",
                                description = "3 sets of 8-12 reps"
                            )
                            ExerciseCard(
                                name = "Chest Cable Flies",
                                description = "4 sets of 8-12 reps"
                            )
                            ExerciseCard(name = "Dumbbell Curls", description = "4 sets of 8-12 reps")
                            ExerciseCard(name = "Hammer Curls", description = "4 sets of 8-12 reps")
                        }
                    }
                    "Day B" -> {
                        Column(modifier = Modifier.padding(10.dp)) {
                            ExerciseCard(name = "Deadlifts", description = "3 sets of 5 reps")
                            ExerciseCard(name = "Chin Ups", description = "3 sets of 8-12 reps")
                            ExerciseCard(name = "Cable Rows", description = "3 sets of 8-12 reps")
                            ExerciseCard(name = "Dumbbell Rows", description = "2 sets of 15 reps")
                            ExerciseCard(name = "Dips", description = "3 sets of 8-12 reps")
                            ExerciseCard(name = "Leg Press", description = "3 sets of 8-12 reps")
                            ExerciseCard(name = "Calf Raises", description = "3 sets of 15 reps")
                        }
                    }
                    "Day C" -> {
                        Column(modifier = Modifier.padding(10.dp)) {
                            ExerciseCard(name = "Bench Press", description = "5 sets of 5 reps")
                            ExerciseCard(
                                name = "Overhead Barbell Press",
                                description = "3 sets of 8-12 reps"
                            )
                            ExerciseCard(
                                name = "Tricep Extensions",
                                description = "3 sets of 8-12 reps"
                            )
                            ExerciseCard(
                                name = "Overhead Dumbbell Press",
                                description = "3 sets of 8-12 reps"
                            )
                            ExerciseCard(
                                name = "Overhead Dumbbell Extensions",
                                description = "3 sets of 8-12 reps"
                            )
                            ExerciseCard(
                                name = "Dumbbell Shoulder Flies",
                                description = "3 sets of 20 reps, alternating side and front"
                            )
                        }
                    }
                    "Day D" -> {
                        Column(modifier = Modifier.padding(10.dp)) {
                            ExerciseCard(name = "Squats", description = "5 sets of 5 reps")
                            ExerciseCard(name = "Deadlifts", description = "3 sets of 8-12 reps")
                            ExerciseCard(name = "Leg Press", description = "3 sets of 8-12 reps")
                            ExerciseCard(name = "Calf Raises", description = "3 sets of 15 reps")
                            ExerciseCard(name = "Hamstring Curls", description = "3 sets of 8-12 reps")
                            ExerciseCard(name = "Dumbbell Curls", description = "3 sets of 8-12 reps")
                        }
                    }
                }
            }
        }
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

private suspend fun getRoutine(
    context: Context,
    viewModel: MainViewModel,
    routineId: Int
): Routine {
    val routine = viewModel.getRoutine(routineId)
    if (routine == null) {
        Toast.makeText(context, context.getString(R.string.exercises_error_findroutine), Toast.LENGTH_LONG).show()
        return Routine.empty
    }
    return routine
}
