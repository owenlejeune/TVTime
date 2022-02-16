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
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.api.tmdb.DetailService
import com.owenlejeune.tvtime.api.tmdb.MoviesService
import com.owenlejeune.tvtime.utils.TmdbUtils
import com.owenlejeune.tvtime.api.tmdb.TvService
import com.owenlejeune.tvtime.api.tmdb.model.*
import com.owenlejeune.tvtime.extensions.dpToPx
import com.owenlejeune.tvtime.ui.components.BackdropImage
import com.owenlejeune.tvtime.ui.components.ChipGroup
import com.owenlejeune.tvtime.ui.components.MinLinesText
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

    val scrollState = rememberScrollState()

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .verticalScroll(state = scrollState)
    ) {
        val (
            backButton, backdropImage, posterImage, titleText, contentColumn
        ) = createRefs()

        Backdrop(
            modifier = Modifier.constrainAs(backdropImage) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            },
            mediaItem = mediaItem
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

        TitleText(
            modifier = Modifier.constrainAs(titleText) {
                bottom.linkTo(posterImage.bottom)
                start.linkTo(posterImage.end, margin = 8.dp)
                end.linkTo(parent.end, margin = 16.dp)
            },
            mediaItem = mediaItem
        )

        BackButton(
            modifier = Modifier.constrainAs(backButton) {
                top.linkTo(parent.top)//, 8.dp)
                start.linkTo(parent.start, 12.dp)
                bottom.linkTo(posterImage.top)
            },
            appNavController = appNavController
        )

        ContentColumn(
            modifier = Modifier.constrainAs(contentColumn) {
               top.linkTo(backdropImage.bottom)//, margin = 8.dp)
            },
            itemId = itemId,
            mediaItem = mediaItem,
            service = service,
            mediaType = type
        )
    }
}

@Composable
private fun Backdrop(modifier: Modifier, mediaItem: MutableState<DetailedItem?>) {
//        val images = remember { mutableStateOf<ImageCollection?>(null) }
//        itemId?.let {
//            if (images.value == null) {
//                fetchImages(itemId, service, images)
//            }
//        }
    BackdropImage(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp),
        imageUrl = TmdbUtils.getFullBackdropPath(mediaItem.value),
//            collection = images.value
    )
}

@Composable
private fun TitleText(modifier: Modifier, mediaItem: MutableState<DetailedItem?>) {
    Text(
        text = mediaItem.value?.title ?: "",
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
            .padding(start = 16.dp, end = 16.dp)
            .fillMaxWidth(.6f),
        style = MaterialTheme.typography.headlineMedium,
        textAlign = TextAlign.Start,
        softWrap = true
    )
}

@Composable
private fun BackButton(modifier: Modifier, appNavController: NavController) {
    IconButton(
        onClick = { appNavController.popBackStack() },
        modifier = modifier
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
}

@Composable
private fun ContentColumn(modifier: Modifier,
                          itemId: Int?,
                          mediaItem: MutableState<DetailedItem?>,
                          service: DetailService,
                          mediaType: DetailViewType
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
        if (mediaType == DetailViewType.MOVIE) {
            MiscMovieDetails(mediaItem = mediaItem, service as MoviesService)
        } else {
            MiscTvDetails(mediaItem = mediaItem)
        }

        OverviewCard(mediaItem = mediaItem)

        CastCard(itemId = itemId, service = service)
    }
}

@Composable
private fun MiscTvDetails(mediaItem: MutableState<DetailedItem?>) {

}

@Composable
private fun MiscMovieDetails(mediaItem: MutableState<DetailedItem?>, service: MoviesService) {
    mediaItem.value?.let { mi ->
        val movie = mi as DetailedMovie

        val contentRating = remember { mutableStateOf("") }
        fetchMovieContentRating(movie.id, service, contentRating)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(start = 8.dp, end = 10.dp, bottom = 8.dp)
            ) {
                val releaseYear = TmdbUtils.getMovieReleaseYear(movie)
                Text(text = releaseYear, color = MaterialTheme.colorScheme.onBackground)
                val runtime = TmdbUtils.convertRuntimeToHoursMinutes(movie)
                Text(
                    text = runtime,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(start = 12.dp)
                )
                Text(
                    text = contentRating.value,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }

            ChipGroup(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(bottom = 8.dp),
                chips = movie.genres.map { it.name }
            )
        }
    }
}

@Composable
private fun OverviewCard(mediaItem: MutableState<DetailedItem?>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(bottom = 16.dp),
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
}

@Composable
private fun CastCard(itemId: Int?, service: DetailService) {
    val castAndCrew = remember { mutableStateOf<CastAndCrew?>(null) }
    itemId?.let {
        if (castAndCrew.value == null) {
            fetchCastAndCrew(itemId, service, castAndCrew)
        }
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

                CastCrewCard(person = castMember)
            }
        }
    }
}

@Composable
private fun CastCrewCard(person: Person) {
    val context = LocalContext.current
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
                data = TmdbUtils.getFullPersonImagePath(person),
                builder = {
                    transformations(RoundedCornersTransformation(5f.dpToPx(context)))
                    placeholder(R.drawable.placeholder)
                }
            ),
            contentDescription = ""
        )
        MinLinesText(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp),
            minLines = 2,
            text = person.name,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.bodyMedium
        )
        MinLinesText(
            modifier = Modifier
                .fillMaxWidth(),
            minLines = 2,
            text = when (person) {
                is CastMember -> person.character
                is CrewMember -> person.job
                else -> ""
            },
            style = MaterialTheme.typography.bodySmall
        )
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

private fun fetchMovieContentRating(id: Int, service: MoviesService, contentRating: MutableState<String>) {
    CoroutineScope(Dispatchers.IO).launch {
        val results = service.getReleaseDates(id)
        if (results.isSuccessful) {
            val cr = TmdbUtils.getMovieRating(results.body())
            withContext(Dispatchers.Main) {
                contentRating.value = cr
            }
        }
    }
}

enum class DetailViewType {
    MOVIE,
    TV
}