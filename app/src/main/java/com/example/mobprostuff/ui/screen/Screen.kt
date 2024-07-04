package com.example.mobprostuff.ui.screen

import android.net.Uri

sealed class Screen(val route: String) {
    data object Home : Screen("mainScreen")

    data object Result : Screen("resultScreen/{url}/{searchQuery}") {
        fun createRoute(url: String, searchQuery: String): String {
            val encodedUrl = Uri.encode(url)
            return "resultScreen/$encodedUrl/$searchQuery"
        }
    }
}