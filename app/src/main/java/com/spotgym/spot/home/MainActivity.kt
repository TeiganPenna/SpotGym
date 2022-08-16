package com.spotgym.spot.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.spotgym.spot.ui.theme.SpotTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SpotTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = Routes.Home.route) {
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

sealed class Routes(val route: String) {
    object Home : Routes("home")
    object Routine : Routes("routine")
}
