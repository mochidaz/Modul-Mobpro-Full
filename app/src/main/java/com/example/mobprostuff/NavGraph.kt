package com.example.mobprostuff

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mobprostuff.ui.screen.MainScreen
import com.example.mobprostuff.ui.screen.Screen

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
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
        }
    }
}