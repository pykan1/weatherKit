package com.example.weather.screen.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.weather.screen.main.MainScreen
import com.example.weather.screen.map.MapScreen

sealed class Screen(
    val screen: String
) {

    data object MainScreen: Screen(screen = "graph_screen/{city_id}/{city_name}") {
        fun setCity(city_id: Int, city_name: String): String {
            return "graph_screen/$city_id/$city_name"
        }
    }
    data object MapScreen: Screen(screen = "map_screen")

}

@Composable
fun SetupNavHost() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.MapScreen.screen
    ) {

        composable(
            route = Screen.MainScreen.screen,
            arguments = listOf(
                navArgument("city_id"){
                    type = NavType.IntType
                    defaultValue = 0
                },
                navArgument("city_name"){
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ){
            val city_id: Int? = it.arguments?.getInt("city_id")
            val city_name: String? = it.arguments?.getString("city_name")
            MainScreen(city_id?: 0, city_name?: "Undefined")
        }
        composable(
            route = Screen.MapScreen.screen
        ){
            MapScreen(navController)
        }

    }
}

