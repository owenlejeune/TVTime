package com.owenlejeune.tvtime.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.owenlejeune.tvtime.api.tmdb.DetailService
import com.owenlejeune.tvtime.api.tmdb.MoviesService
import com.owenlejeune.tvtime.api.tmdb.TmdbUtils
import com.owenlejeune.tvtime.api.tmdb.TvService
import com.owenlejeune.tvtime.api.tmdb.model.DetailedItem
import com.owenlejeune.tvtime.api.tmdb.model.Image
import com.owenlejeune.tvtime.api.tmdb.model.ImageCollection
import com.owenlejeune.tvtime.ui.components.BackdropImage
import com.owenlejeune.tvtime.ui.components.PosterItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun DetailView(
    appNavController: NavController,
    itemId: Int?,
    type: DetailViewType
) {
    val context = LocalContext.current
    val service = when(type) {
        DetailViewType.MOVIE -> MoviesService()
        DetailViewType.TV -> TvService()
    }

    val mediaItem = remember { mutableStateOf<DetailedItem?>(null) }
    itemId?.let {
        fetchMediaItem(itemId, service, mediaItem)
    }

    val images = remember { mutableStateOf<ImageCollection?>(null) }
    itemId?.let {
        fetchImages(itemId, service, images)
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        val (
            backButton,
            backdropImage,
            posterImage,
            title
        ) = createRefs()

        BackdropImage(
            modifier = Modifier
                .constrainAs(backdropImage) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                }
                .fillMaxWidth()
                .size(0.dp, 280.dp),
            imageUrl = TmdbUtils.getFullBackdropPath(mediaItem.value),
            collection = images.value
        )

        PosterItem(
            mediaItem = mediaItem.value,
            modifier = Modifier
                .constrainAs(posterImage) {
                    bottom.linkTo(title.top, margin = 8.dp)
                    start.linkTo(parent.start, margin = 16.dp)
                    top.linkTo(backButton.bottom, margin = 8.dp)
                }
        )

        Text(
            text = mediaItem.value?.title ?: "",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .constrainAs(title) {
                    bottom.linkTo(backdropImage.bottom, margin = 8.dp)
                    start.linkTo(parent.start, margin = 20.dp)
                    end.linkTo(parent.end, margin = 16.dp)
                }
                .padding(start = 16.dp, end = 16.dp)
                .fillMaxWidth(),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Start,
            softWrap = true
        )

        IconButton(
            onClick = { appNavController.popBackStack() },
            modifier = Modifier
                .constrainAs(backButton) {
                    top.linkTo(parent.top, 16.dp)
                    start.linkTo(parent.start, 12.dp)
                }
                .background(brush = Brush.radialGradient(colors = listOf(Color.Black, Color.Transparent)))
                .wrapContentSize()
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

private fun fetchMediaItem(id: Int, service: DetailService, mediaItem: MutableState<DetailedItem?>) {
    CoroutineScope(Dispatchers.IO).launch {
        val response = service.getById(id)
        if (response.isSuccessful) {
            withContext(Dispatchers.Main) {
                mediaItem.value = response.body()
            }
        }
    }
}

private fun fetchImages(id: Int, service: DetailService, images: MutableState<ImageCollection?>) {
    CoroutineScope(Dispatchers.IO).launch {
        val response = service.getImages(id)
        if (response.isSuccessful) {
            withContext(Dispatchers.Main) {
                images.value = response.body()
            }
        }
    }
}

enum class DetailViewType {
    MOVIE,
    TV
}