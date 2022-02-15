package com.owenlejeune.tvtime.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.api.tmdb.DetailService
import com.owenlejeune.tvtime.api.tmdb.MoviesService
import com.owenlejeune.tvtime.api.tmdb.TmdbUtils
import com.owenlejeune.tvtime.api.tmdb.TvService
import com.owenlejeune.tvtime.api.tmdb.model.CastAndCrew
import com.owenlejeune.tvtime.api.tmdb.model.DetailedItem
import com.owenlejeune.tvtime.api.tmdb.model.ImageCollection
import com.owenlejeune.tvtime.extensions.dpToPx
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
        if (mediaItem.value == null) {
            fetchMediaItem(itemId, service, mediaItem)
        }
    }

    val images = remember { mutableStateOf<ImageCollection?>(null) }
    itemId?.let {
        if (images.value == null) {
            fetchImages(itemId, service, images)
        }
    }

    val scrollState = rememberScrollState()

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .verticalScroll(state = scrollState)
    ) {
        val (
            backButton,
            backdropImage,
            posterImage,
            titleText,
            contentColumn
        ) = createRefs()

        BackdropImage(
            modifier = Modifier
                .constrainAs(backdropImage) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                }
                .fillMaxWidth()
                .height(280.dp),
            imageUrl = TmdbUtils.getFullBackdropPath(mediaItem.value),
//            collection = images.value
        )

        PosterItem(
            mediaItem = mediaItem.value,
            modifier = Modifier
                .constrainAs(posterImage) {
                    bottom.linkTo(backdropImage.bottom)
                    start.linkTo(parent.start, margin = 16.dp)
                    top.linkTo(backButton.bottom)
                }
        )

        Text(
            text = mediaItem.value?.title ?: "",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .constrainAs(titleText) {
                    bottom.linkTo(posterImage.bottom)
                    start.linkTo(posterImage.end, margin = 8.dp)
                    end.linkTo(parent.end, margin = 16.dp)
                }
                .padding(start = 16.dp, end = 16.dp)
                .fillMaxWidth(.6f),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Start,
            softWrap = true
        )

        IconButton(
            onClick = { appNavController.popBackStack() },
            modifier = Modifier
                .constrainAs(backButton) {
                    top.linkTo(parent.top)//, 8.dp)
                    start.linkTo(parent.start, 12.dp)
                    bottom.linkTo(posterImage.top)
                }
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.Black,
                            Color.Transparent
                        )
                    )
                )
                .wrapContentSize()
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        val castAndCrew = remember { mutableStateOf<CastAndCrew?>(null) }
        itemId?.let {
            if (castAndCrew.value == null) {
                fetchCastAndCrew(itemId, service, castAndCrew)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 16.dp)
                .constrainAs(contentColumn) {
                    top.linkTo(backdropImage.bottom, margin = 8.dp)
                }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(10.dp),
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                elevation = 8.dp
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    text = mediaItem.value?.overview ?: "",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(10.dp),
                backgroundColor = MaterialTheme.colorScheme.primary,
                elevation = 8.dp
            ) {
                LazyRow(modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
                ) {
                    items(castAndCrew.value?.cast?.size ?: 0) { i ->
                        val castMember = castAndCrew.value!!.cast[i]
                        Column(
                            modifier = Modifier
                                .width(124.dp)
                                .wrapContentHeight()
                                .padding(end = 12.dp)
                        ) {
                            Image(
                                modifier = Modifier
                                    .size(width = 120.dp, height = 180.dp),
                                painter = rememberImagePainter(
                                    data = TmdbUtils.getFullPersonImagePath(castMember),
                                    builder = {
                                        transformations(RoundedCornersTransformation(5f.dpToPx(context)))
                                        placeholder(R.drawable.placeholder)
                                    }
                                ),
                                contentDescription = ""
                            )
                            val nameLineHeight = MaterialTheme.typography.bodyMedium.fontSize*4/3
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 5.dp)
                                    .sizeIn(
                                        minHeight = with(LocalDensity.current) {
                                            (nameLineHeight * 2).toDp()
                                        }
                                    ),
                                text = castMember.name,
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.bodyMedium,
                                lineHeight = nameLineHeight
                            )
                            val characterLineHeight = MaterialTheme.typography.bodySmall.fontSize*4/3
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .sizeIn(
                                        minHeight = with(LocalDensity.current) {
                                            (characterLineHeight * 2).toDp()
                                        }
                                    ),
                                text = castMember.character,
                                style = MaterialTheme.typography.bodySmall,
                                lineHeight = characterLineHeight
                            )
                        }
                    }
                }
            }
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

private fun fetchCastAndCrew(id: Int, service: DetailService, castAndCrew: MutableState<CastAndCrew?>) {
    CoroutineScope(Dispatchers.IO).launch {
        val response = service.getCastAndCrew(id)
        if (response.isSuccessful) {
            withContext(Dispatchers.Main) {
                castAndCrew.value = response.body()
            }
        }
    }
}

enum class DetailViewType {
    MOVIE,
    TV
}