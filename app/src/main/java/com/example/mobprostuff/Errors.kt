package com.example.mobprostuff

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun ErrorHint(isError: Boolean, message: String) {
    if (isError) {
        Text(
            text = message,
        )
    }
}