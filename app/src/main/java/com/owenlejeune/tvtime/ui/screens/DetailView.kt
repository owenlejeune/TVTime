package com.owenlejeune.tvtime.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.owenlejeune.tvtime.api.tmdb.DetailService
import com.owenlejeune.tvtime.api.tmdb.MoviesService
import com.owenlejeune.tvtime.api.tmdb.TvService
import com.owenlejeune.tvtime.api.tmdb.model.DetailedItem
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
    val mediaItem = remember { mutableStateOf<DetailedItem?>(null) }
    val service = when(type) {
        DetailViewType.MOVIE -> MoviesService()
        DetailViewType.TV -> TvService()
    }
    itemId?.let {
        fetchMediaItem(itemId, service, mediaItem)
    }

    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        val (
            posterImage,
            title
        ) = createRefs()

        PosterItem(
            appNavController = appNavController,
            mediaItem = mediaItem.value,
            modifier = Modifier
                .constrainAs(posterImage) {
                    top.linkTo(parent.top, margin = 16.dp)
                    start.linkTo(parent.start, margin = 16.dp)
                }
        )
    }
}

private fun fetchMediaItem(id: Int, service: DetailService, mediaItem: MutableState<DetailedItem?>) {
    CoroutineScope(Dispatchers.IO).launch {
        val response = service.getById(id)
        if (response.isSuccessful) {
            withContext(Dispatchers.Main) {
                mediaItem.value = response.body()!!
            }
        }
    }
}

enum class DetailViewType {
    MOVIE,
    TV
}