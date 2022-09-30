package com.spotgym.spot.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.spotgym.spot.ui.theme.SpotTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @ExperimentalAnimationApi
    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SpotTheme {
                val navController = rememberAnimatedNavController()

                AnimatedNavHost(
                    navController = navController,
                    startDestination = Routes.Home.route,
                    enterTransition = { EnterTransition.None },
                    exitTransition = { ExitTransition.None },
                    popEnterTransition = { EnterTransition.None },
                    popExitTransition = { ExitTransition.None },
                ) {
                    composable(Routes.Home.route) {
                        SpotHome(
                            onRoutineClicked = { routineId ->
                                navController.navigate(Routes.Routine.route + "/$routineId")
                            }
                        )
                    }
                    composable(
                        route = Routes.Routine.route + "/{routineId}",
                        arguments = listOf(
                            navArgument("routineId") {
                                type = NavType.IntType
                            },
                        )
                    ) { backStackEntry ->
                        val routineId = backStackEntry.arguments?.getInt("routineId")
                        ExercisesPage(
                            routineId = routineId!!
                        )
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
