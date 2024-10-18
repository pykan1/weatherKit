package com.example.weather.screen.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weather.screen.main.MainScreen
import com.example.weather.screen.map.MapScreen

sealed class Screen(
    val screen: String
) {

    data object MainScreen: Screen(screen = "graph_screen")
    data object MapScreen: Screen(screen = "map_screen")

}

@Composable
fun SetupNavHost() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.MainScreen.screen
    ) {

        composable(route = Screen.MainScreen.screen) {
            MainScreen(3, "sa")
        }

        composable(route = Screen.MapScreen.screen) {
            MapScreen()
        }

    }
}

