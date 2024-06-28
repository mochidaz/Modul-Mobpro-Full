package com.example.mobprostuff

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                )
            )
        }
    ) {
            padding -> ScreenContent(Modifier.padding(padding))
    }
}

@Composable
fun ScreenContent(modifier: Modifier = Modifier) {
    var vectorA by remember {
        mutableStateOf("")
    }

    var vectorB by remember {
        mutableStateOf("")
    }

    var vecAErr: Errors? by remember {
        mutableStateOf(null)
    }

    var vecBErr: Errors? by remember {
        mutableStateOf(null)
    }

    var dotProduct: Float? by remember {
        mutableStateOf(null)
    }

    Column (
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),

        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.dp_intro),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = vectorA,
            onValueChange = { vectorA = it },
            label = { Text(text = stringResource(id = R.string.dp_vector_a)) },
            trailingIcon = { IconPicker(vecAErr) },
            supportingText = { ErrorHint(vecAErr) },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = vectorB,
            onValueChange = { vectorB = it },
            label = { Text(text = stringResource(id = R.string.dp_vector_b)) },
            trailingIcon = { IconPicker(vecBErr) },
            supportingText = { ErrorHint(vecBErr) },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                vecAErr = validateInput(vectorA)
                vecBErr = validateInput(vectorB)

                if (vecAErr != null || vecBErr != null) {
                    return@Button
                }

                vecAErr = isDimEqual(vectorA, vectorB)
                vecBErr = isDimEqual(vectorA, vectorB)

                if (vecAErr != null || vecBErr != null) {
                    return@Button
                }

                val vecA = deserializeInput(vectorA)
                val vecB = deserializeInput(vectorB)

                dotProduct = calculateDotProduct(vecA, vecB)
                vecAErr = null
                vecBErr = null
            },
            modifier = Modifier.padding(top = 8.dp),
            contentPadding = PaddingValues(horizontal=32.dp, vertical=16.dp),
        ) {
            Text(text = stringResource(id = R.string.dp_calculate))
        }

        if (dotProduct != null) {
            Divider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 1.dp
            )

            Text(
                text = stringResource(id = R.string.dp_x, dotProduct!!),
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}
