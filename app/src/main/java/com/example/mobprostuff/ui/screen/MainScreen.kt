package com.example.mobprostuff.ui.screen

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.mobprostuff.R
import com.example.mobprostuff.auth.signIn
import com.example.mobprostuff.auth.signOut
import com.example.mobprostuff.model.Track
import com.example.mobprostuff.model.User
import com.example.mobprostuff.network.NazrinAPI
import com.example.mobprostuff.network.UserDataStore
import com.example.mobprostuff.utils.SettingsDataStore
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

    var showDialog by remember {
        mutableStateOf(false)
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
    ) { padding ->
        ScreenContent(navController, Modifier.padding(padding))

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
    }
}

@Composable
fun ScreenContent(navController: NavHostController, modifier: Modifier = Modifier) {
    var url by remember {
        mutableStateOf("")
    }

    var searchQuery by remember {
        mutableStateOf("")
    }

    val context = LocalContext.current

    Column(modifier = modifier.padding(16.dp)) {
        OutlinedTextField(
            value = url,
            onValueChange = { url = it },
            label = { Text("URL") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search Query") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        Button(
            onClick = {
                if (url.isEmpty() || searchQuery.isEmpty()) {
                Toast.makeText(context, "URL and Search Query cannot be empty", Toast.LENGTH_SHORT).show()
                  return@Button
                }

                navController.navigate(Screen.Result.createRoute(url, searchQuery))
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(stringResource(id = R.string.submit))
        }
    }
}
