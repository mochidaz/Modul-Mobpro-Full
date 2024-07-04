package com.example.mobprostuff

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mobprostuff.ui.screen.MainScreen
import com.example.mobprostuff.ui.screen.ResultScreen
import com.example.mobprostuff.ui.screen.Screen

@Composable
fun SetupNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(route = Screen.Home.route) {
            MainScreen(navController)
        }
        composable(
            route = Screen.Result.route,
            arguments = listOf(
                navArgument("url") { type = NavType.StringType },
                navArgument("searchQuery") { type = NavType.StringType }
            )
        ) {
            backStackEntry ->
            val url = backStackEntry.arguments?.getString("url")
            val searchQuery = backStackEntry.arguments?.getString("searchQuery")
            ResultScreen(navController, url, searchQuery)
        }
    }
}