package com.example.mobprostuff

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun Gallery(sprite: Sprite, onClick: () -> Unit = {}) {
    MainScreen {
        modifier ->
        Column (
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = sprite.imageResId),
                contentDescription = stringResource(R.string.img, sprite.name),
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(132.dp )
            )

            Text(
                text = sprite.name,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(top = 16.dp)
            )

            Button(
                onClick = {onClick()},
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .padding(top = 24.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                Text(text = stringResource(R.string.next))
            }
        }
    }
}