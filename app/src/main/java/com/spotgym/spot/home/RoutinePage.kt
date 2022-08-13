package com.spotgym.spot.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun RoutinePage(
    routineId: String
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(routineId) }
            )
        }
    ) { contentPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            color = MaterialTheme.colors.background
        ) {
            when (routineId) {
                "Day A" -> {
                    Column(modifier = Modifier.padding(10.dp)) {
                        ExerciseCard(name = "Bench Press", description = "3 sets of 5 reps")
                        ExerciseCard(name = "Paused Bench Press", description = "4 sets of 3 reps")
                        ExerciseCard(name = "Incline Dumbbell Press", description = "3 sets of 8-12 reps")
                        ExerciseCard(name = "Chest Cable Flies", description = "4 sets of 8-12 reps")
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
                        ExerciseCard(name = "Overhead Barbell Press", description = "3 sets of 8-12 reps")
                        ExerciseCard(name = "Tricep Extensions", description = "3 sets of 8-12 reps")
                        ExerciseCard(name = "Overhead Dumbbell Press", description = "3 sets of 8-12 reps")
                        ExerciseCard(name = "Overhead Dumbbell Extensions", description = "3 sets of 8-12 reps")
                        ExerciseCard(name = "Dumbbell Shoulder Flies", description = "3 sets of 20 reps, alternating side and front")
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