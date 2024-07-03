package com.example.mobprostuff

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mobprostuff.ui.screen.DetailScreen
import com.example.mobprostuff.ui.screen.KEY_STUDENT_ID
import com.example.mobprostuff.ui.screen.MainScreen
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
        composable(route = Screen.DetailNewForm.route) {
            DetailScreen(navController)
        }
        composable(
            route = Screen.DetailEditForm.route,
            arguments = listOf(navArgument(KEY_STUDENT_ID) {
                type = NavType.StringType
            }),

        ) {
            navBackStackEntry ->
            val id = navBackStackEntry.arguments?.getString(KEY_STUDENT_ID)
            DetailScreen(navController, id)
        }
    }
}