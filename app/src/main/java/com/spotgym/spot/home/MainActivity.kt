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

                NavHost(navController = navController, startDestination = Routes.Routines.route) {
                    composable(Routes.Routines.route) { RoutinesHome() }
                }
            }
        }
    }
}

sealed class Routes(val route: String) {
    object Routines : Routes("routines")
}
