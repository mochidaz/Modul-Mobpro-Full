package com.example.mobprostuff.ui.screen

import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.request.ImageRequest
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.example.mobprostuff.R
import com.example.mobprostuff.auth.signIn
import com.example.mobprostuff.model.Character
import com.example.mobprostuff.network.API
import com.example.mobprostuff.viewmodels.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun UpdateCharacterDialog(
    data: Character,
    bitmap: Bitmap?,
    launcher: ManagedActivityResultLauncher<CropImageContractOptions, CropImageView.CropResult>,
    onDismissRequest: () -> Unit,
    onConfirmation: (String, String) -> Unit,
    viewModel: MainViewModel,
    userId: String,
) {
    var name by remember { mutableStateOf(data.name) }
    var description by remember { mutableStateOf(data.description) }
    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier.padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {

            if (userId.isNotEmpty() && userId == data.userId) {
                IconButton(onClick = {
                    showDeleteDialog = true
                }, modifier = Modifier.align(Alignment.End)) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_delete_24),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (bitmap == null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(API.getCharacterImage(data.imageUrl))
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = R.drawable.baseline_downloading_24),
                        error = painterResource(id = R.drawable.baseline_broken_image_24),
                        modifier = Modifier
                            .aspectRatio(1f)
                            .fillMaxWidth()
                            .padding(4.dp)
                    )
                } else {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .fillMaxWidth()
                            .padding(4.dp)
                    )
                }
                if (userId.isNotEmpty() && userId == data.userId) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(text = stringResource(id = R.string.name)) },
                        maxLines = 1,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text(text = stringResource(id = R.string.description)) },
                        maxLines = 1,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Done
                        ),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    if (bitmap != null) {
                        Text(text = "An image to upload")
                    }
                    Button(onClick = {
                        val options = CropImageContractOptions(
                            null, CropImageOptions(
                                imageSourceIncludeGallery = true,
                                imageSourceIncludeCamera = false,
                                fixAspectRatio = true
                            )
                        )
                        launcher.launch(options)
                    }, modifier = Modifier.padding(10.dp)) {
                        Text(text = "Upload Image")
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        OutlinedButton(
                            onClick = { onDismissRequest() },
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text(text = stringResource(R.string.cancel))
                        }
                        OutlinedButton(
                            onClick = { onConfirmation(name, description) },
                            enabled = name.isNotEmpty() && description.isNotEmpty(),
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text(text = stringResource(R.string.save))
                        }
                        if (showDeleteDialog) {
                            DeleteDialog(
                                onDismissRequest = onDismissRequest,
                                onConfirmation = {
                                    viewModel.deleteCharacter(data.id, userId)
                                    showDeleteDialog = false
                                }
                            )
                        }
                    }
                } else {
                    Text(text = "Name: ${data.name}")
                    Text(text = "Description: ${data.description}")
                }
            }
        }
    }
}

@Composable
fun DeleteDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier.padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = "Are you sure you want to delete this character?")

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    OutlinedButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(text = stringResource(R.string.cancel))
                    }
                    OutlinedButton(
                        onClick = {
                            onConfirmation()
                            onDismissRequest()
                        },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(text = stringResource(R.string.confirm_delete))
                    }
                }
            }
        }
    }
}
