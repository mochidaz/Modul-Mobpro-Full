package com.example.mobprostuff

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource

@Composable
fun ErrorHint(error: Errors?) {
    if (error != null) {
        Text(
            text = when (error) {
                Errors.ErrInputEmpty -> stringResource(id = R.string.dp_vector_error)
                Errors.ErrInputNotEqual -> stringResource(id = R.string.dp_vector_not_equal_error)
                Errors.ErrInputInvalid -> stringResource(id = R.string.dp_vector_invalid_input_error)
            },
            color = Color.Red
        )
    }
}

enum class Errors {
    ErrInputEmpty,
    ErrInputNotEqual,
    ErrInputInvalid,
}