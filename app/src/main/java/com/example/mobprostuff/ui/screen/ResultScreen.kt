package com.example.mobprostuff.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.mobprostuff.model.Track
import com.example.mobprostuff.network.NazrinAPI
import com.example.mobprostuff.viewmodels.MainViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.example.mobprostuff.R
import com.example.mobprostuff.utils.SettingsDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(navController: NavHostController, url: String? = null, searchQuery: String? = null) {
    val viewModel: MainViewModel = viewModel()

    val dataStore = SettingsDataStore(LocalContext.current)
    val showList by dataStore.layoutFlow.collectAsState(true)

    LaunchedEffect(key1 = url, key2 = searchQuery) {
        viewModel.search(url ?: "", searchQuery ?: "")
    }

    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.result_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(id = R.string.back))
                    }
                },
                actions = {
                    IconButton(onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            dataStore.saveLayout(!showList)
                        }
                    }) {
                        Icon(
                            painter = painterResource(
                                if (showList) R.drawable.baseline_grid_view_24
                                else R.drawable.baseline_view_list_24
                            ),
                            contentDescription = stringResource(
                                if (showList) R.string.grid
                                else R.string.list
                            ),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    Column {
                        Text(text = stringResource(id = R.string.searching))
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                } else {
                    Text(text = stringResource(id = R.string.search_done, searchResults.size))
                }
            }


            Divider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = Color.Gray
            )

            when {
                error != null -> {
                    Text("Error: $error")
                }
                searchResults.isNotEmpty() -> {
                    if (showList) {
                        ItemList(searchResults)
                    } else {
                        ItemGrid(searchResults)
                    }
                }
            }

            if (searchResults.isEmpty() and !isLoading) {
                Text(stringResource(id = R.string.not_found))
            }
        }
    }
}

@Composable
fun ItemList(data: List<Track>) {
    LazyColumn {
        items(data) { track ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
                border = BorderStroke(1.dp, Color.Gray),
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    track.albumImg?.let {
                        val painter = rememberAsyncImagePainter(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(it)
                                .build()
                        )
                        val painterState = painter.state

                        Box {
                            Image(
                                painter = painter,
                                contentDescription = "Album Image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                            )

                            when (painterState) {
                                is AsyncImagePainter.State.Loading -> {
                                    CircularProgressIndicator(
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                                is AsyncImagePainter.State.Error -> {
                                    Image(
                                        painter = painterResource(R.drawable.baseline_broken_image_24), // Replace with your error drawable
                                        contentDescription = "Error Image",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(120.dp)
                                            .clip(RoundedCornerShape(4.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                else -> Unit
                            }
                        }
                    }
                    track.album?.let { Text(text = "Album: $it", fontWeight = FontWeight.Bold) }
                    track.trackNumber?.let { Text(text = "Track Number: $it") }
                    track.arrangementTitle?.let { Text(text = "Arrangement Title: $it") }
                    track.translatedName?.let { Text(text = "Translated Name: $it") }
                    track.arrangement?.let { Text(text = "Arrangement: $it") }
                    track.source?.let { Text(text = "Source: $it") }
                    track.vocals?.let { Text(text = "Vocals: $it") }
                    track.lyrics?.let { Text(text = "Lyrics: $it") }
                    track.originalTitle?.let { Text(text = "Original Title: $it") }
                    track.guitar?.let { Text(text = "Guitar: $it") }
                    track.note?.let { Text(text = "Note: $it") }
                    track.from?.let { Text(text = "From: $it") }
                    track.genre?.let { Text(text = "Genre: $it") }
                }
            }
        }
    }
}

@Composable
fun ItemGrid(data: List<Track>) {
    LazyVerticalStaggeredGrid(
        modifier = Modifier.fillMaxSize(),
        columns = StaggeredGridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp
    ) {
        items(data) { track ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
                border = BorderStroke(1.dp, Color.Gray),
            ) {
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    track.albumImg?.let {
                        val painter = rememberAsyncImagePainter(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(it)
                                .build()
                        )
                        val painterState = painter.state

                        Box {
                            Image(
                                painter = painter,
                                contentDescription = "Album Image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                contentScale = ContentScale.Crop
                            )

                            when (painterState) {
                                is AsyncImagePainter.State.Loading -> {
                                    CircularProgressIndicator(
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                                is AsyncImagePainter.State.Error -> {
                                    Image(
                                        painter = painterResource(R.drawable.baseline_broken_image_24), // Replace with your error drawable
                                        contentDescription = "Error Image",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(120.dp)
                                            .clip(RoundedCornerShape(4.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                else -> Unit
                            }
                        }
                    }

                    track.album?.let {
                        Text(text = "Album: $it", fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    track.trackNumber?.let {
                        Text(text = "Track: $it", maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    track.arrangementTitle?.let {
                        Text(text = "Arrangement: $it", maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    track.translatedName?.let {
                        Text(text = "Translated: $it", maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    track.arrangement?.let {
                        Text(text = "Arrangement: $it", maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    track.source?.let {
                        Text(text = "Source: $it", maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    track.vocals?.let {
                        Text(text = "Vocals: $it", maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    track.lyrics?.let {
                        Text(text = "Lyrics: $it", maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    track.originalTitle?.let {
                        Text(text = "Original: $it", maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    track.guitar?.let {
                        Text(text = "Guitar: $it", maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    track.note?.let {
                        Text(text = "Note: $it", maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    track.from?.let {
                        Text(text = "From: $it", maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    track.genre?.let {
                        Text(text = "Genre: $it", maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
    }
}