package com.example.mobprostuff

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import kotlin.math.pow

fun calculateBMI(weight: Float, height: Float): Float {
    return weight / (height / 100).pow(2)
}

fun getCategory(bmi: Float, isMale: Boolean): Int {
    return if (isMale) {
        when {
            bmi < 20.5 -> R.string.underweight
            bmi >= 27.0 -> R.string.overweight
            else -> R.string.ideal
        }
    } else {
        when {
            bmi < 18.5 -> R.string.underweight
            bmi >= 25.0 -> R.string.overweight
            else -> R.string.ideal
        }
    }
}

@Composable
fun IconPicker(isError: Boolean, unit: String) {
    if (isError) {
        Icon(imageVector = Icons.Filled.Warning, contentDescription = null)
    } else {
        Text(text = unit)
    }
}