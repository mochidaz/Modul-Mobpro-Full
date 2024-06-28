package com.example.mobprostuff

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import java.util.Vector

fun calculateDotProduct(v1: Vector<Float>, v2: Vector<Float>): Float {
    return v1.zip(v2).map { it.first * it.second }.sum()
}

fun validateInput(input: String): Errors? {
    if (input.isEmpty()) {
        return Errors.ErrInputEmpty
    }

    if (!input.contains(",")) {
        return Errors.ErrInputInvalid
    }

    return null
}

fun isDimEqual(v1: String, v2: String): Errors? {
    if (v1.split(",").size != v2.split(",").size) {
        return Errors.ErrInputNotEqual
    }

    return null
}

fun deserializeInput(input: String): Vector<Float> {
    val split = input.trim().split(",")

    val vector = Vector<Float>()

    for (i in split) {
        vector.add(i.toFloat())
    }

    return vector
}

fun shareData(context: Context, message: String) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, message)
    }

    if (shareIntent.resolveActivity(context.packageManager) != null) {
        context.startActivity(shareIntent)
    }
}

@Composable
fun IconPicker(isError: Errors?) {
    if (isError != null) {
        Icon(
            imageVector = Icons.Filled.Warning,
            contentDescription = "Error",
        )
    }
}
