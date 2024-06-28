package com.example.mobprostuff

sealed class Screen(val route: String) {
    data object Home : Screen("mainScreen")
    data object Explanation : Screen("explanationScreen")
}