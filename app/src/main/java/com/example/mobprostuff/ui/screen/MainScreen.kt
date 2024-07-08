package com.example.mobprostuff.ui.screen

import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.example.mobprostuff.R
import com.example.mobprostuff.auth.signIn
import com.example.mobprostuff.auth.signOut
import com.example.mobprostuff.model.Character
import com.example.mobprostuff.model.User
import com.example.mobprostuff.network.API
import com.example.mobprostuff.network.UserDataStore
import com.example.mobprostuff.types.Action
import com.example.mobprostuff.utils.getCroppedImage
import com.example.mobprostuff.viewmodels.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController) {
    val context = LocalContext.current

    val dataStore = UserDataStore(context)

    val user by dataStore.userFlow.collectAsState(User())

    Log.d("MainScreen", user.toString())

    var showDialog by remember {
        mutableStateOf(false)
    }

    var showCharacterDialog by remember {
        mutableStateOf(false)
    }

    val bitmap: MutableState<Bitmap?> = remember { mutableStateOf(null) }

    val viewModel: MainViewModel = viewModel()

    val action: MutableState<Action> = remember {
        mutableStateOf(Action.IsCreating)
    }

    val launcher = rememberLauncherForActivityResult(CropImageContract()) {
        bitmap.value = getCroppedImage(context.contentResolver, it)
        if (bitmap.value != null && action.value == Action.IsCreating) showCharacterDialog = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.app_name))
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                actions = {
                    IconButton(onClick = {
                        viewModel.fetchCharacters()
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_refresh_24),
                            contentDescription = "Refresh",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            if (user.userEmail.isEmpty()) {
                                try {
                                    signIn(context, dataStore)
                                } catch (e: Exception) {
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                showDialog = true
                            }
                        }
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_account_circle_24),
                            contentDescription = stringResource(id = R.string.profile),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                if (user.userEmail.isNotEmpty()) {
                    val options = CropImageContractOptions(
                        null, CropImageOptions(
                            imageSourceIncludeGallery = true,
                            imageSourceIncludeCamera = false,
                            fixAspectRatio = true
                        )
                    )
                    launcher.launch(options)
                } else {
                    Toast.makeText(context, "Please sign in first", Toast.LENGTH_SHORT).show()
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.add_student)
                )
            }
        }
    ) { padding ->
        ScreenContent(viewModel, Modifier.padding(padding), bitmap, action, launcher, user.userEmail)

        if (showDialog) {
            ProfileDialog(user = user, onDismissRequest = { showDialog = false }) {
                CoroutineScope(Dispatchers.IO).launch {
                    signOut(context, dataStore)

                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Signed out", Toast.LENGTH_SHORT).show()
                    }
                }
                showDialog = false
            }
        }

        if (showCharacterDialog) {
            CharacterDialog(
                bitmap = bitmap.value,
                onDismissRequest = {
                    showCharacterDialog = false
                    bitmap.value = null
                }) { name, description ->
                viewModel.addCharacter(name, description, bitmap.value!!, user.userEmail)
                showCharacterDialog = false
                bitmap.value = null
            }
        }
    }
}

@Composable
fun ScreenContent(viewModel: MainViewModel, modifier: Modifier = Modifier, bitmap: MutableState<Bitmap?>, action: MutableState<Action>, launcher: ManagedActivityResultLauncher<CropImageContractOptions, CropImageView.CropResult>, userId: String) {
    LaunchedEffect(true) {
        viewModel.fetchCharacters()
    }

    val data by viewModel.characters.collectAsState()

    val loading by viewModel.loading.collectAsState()

    val error = viewModel.error.collectAsState()

    var toUpdate by remember { mutableStateOf(
        Character(
            id = 0,
            name = "",
            description = "",
            imageUrl = "",
            userId = ""
        )
    ) }

    var showUpdateDialog by remember {
        mutableStateOf(false)
    }

    Column(modifier = modifier.padding(16.dp)) {
        if (loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    modifier = Modifier.size(50.dp)
                )
            }
        } else if (error.value.isNotEmpty()) {
            Text(text = error.value, modifier = Modifier.fillMaxWidth(), color = Color.Red, textAlign = TextAlign.Center)
            Button(onClick = {
                viewModel.fetchCharacters()
                bitmap.value = null
                viewModel.clearError()
            }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text(text = "Retry")
            }
        } else {
            ItemList(data ?: emptyList()) {
                toUpdate = it
                showUpdateDialog = true
            }
        }

        if (showUpdateDialog) {

            action.value = Action.IsUpdating

            UpdateCharacterDialog(
                toUpdate,
                bitmap = bitmap.value,
                launcher,
                onDismissRequest = {
                    showUpdateDialog = false
                    bitmap.value = null
                    action.value = Action.IsCreating
               },
                onConfirmation = { name, description ->
                    viewModel.updateCharacter(toUpdate.id, name, description, bitmap.value, userId)
                    showUpdateDialog = false
                    action.value = Action.IsCreating
                    bitmap.value = null
                },
                viewModel,
                userId,
            )
        }
    }
}

@Composable
fun ItemList(data: List<Character>, onClick: (Character) -> Unit = {}) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        items(data) { item ->
            Box(
                modifier = Modifier
                    .clickable {
                        onClick(item)
                    }
                    .padding(4.dp)
                    .border(1.dp, Color.Gray),
                contentAlignment = Alignment.BottomCenter,

            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(API.getCharacterImage(item.imageUrl))
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .background(Color(red = 0f, green = 0f, blue = 0f, alpha = 0.5f))
                        .padding(4.dp)
                ) {
                    Text(
                        text = item.name,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = item.description,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}