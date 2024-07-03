package com.example.mobprostuff.ui.screen

sealed class Screen(val route: String) {
    data object Home : Screen("mainScreen")
    data object DetailNewForm : Screen("detailScreen")
    data object DetailEditForm : Screen("detailScreen/{$KEY_STUDENT_ID}") {
        fun withId(id: String): String {
            return "detailScreen/$id"
        }
    }
}