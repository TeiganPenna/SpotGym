package com.spotgym.spot.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.spotgym.spot.R
import com.spotgym.spot.ui.theme.SpotTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SpotTheme {
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
                        val navController = rememberNavController()

                        NavHost(
                            navController = navController,
                            startDestination = Routes.Home.route
                        ) {
                            composable(Routes.Home.route) {
                                SpotHome(
                                    onRoutineClicked = { routineId ->
                                        navController.navigate(Routes.Routine.route + "/$routineId")
                                    }
                                )
                            }
                            composable(Routes.Routine.route + "/{routineId}") { backStackEntry ->
                                val routineId = backStackEntry.arguments?.getString("routineId")
                                RoutinePage(routineId!!)
                            }
                        }
                    }
                }
            }
        }
    }
}

sealed class Routes(val route: String) {
    object Home : Routes("home")
    object Routine : Routes("routine")
}
