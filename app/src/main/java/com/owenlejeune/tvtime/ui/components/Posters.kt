package com.owenlejeune.tvtime.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.HomePagePerson
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.ImageCollection
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.Person
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.TmdbItem
import com.owenlejeune.tvtime.extensions.dpToPx
import com.owenlejeune.tvtime.extensions.listItems
import com.owenlejeune.tvtime.utils.TmdbUtils

private val POSTER_WIDTH = 120.dp
private val POSTER_HEIGHT = 190.dp

@Composable
fun PosterGrid(
    fetchMedia: (MutableState<List<TmdbItem>>) -> Unit = {},
    onClick: (Int) -> Unit = {}
) {
    val mediaList = remember { mutableStateOf(emptyList<TmdbItem>()) }
    fetchMedia(mediaList)

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = POSTER_WIDTH),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        listItems(mediaList.value) { item ->
            PosterItem(
                modifier = Modifier.padding(5.dp),
                mediaItem = item,
                onClick = onClick
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PeoplePosterGrid(
    fetchPeople: (MutableState<List<HomePagePerson>>) -> Unit = {},
    onClick: (Int) -> Unit = {}
) {
    val peopleList = remember { mutableStateOf(emptyList<HomePagePerson>()) }
    fetchPeople(peopleList)

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = POSTER_WIDTH),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        listItems(peopleList.value) { person ->
            PosterItem(
                url = TmdbUtils.getFullPersonImagePath(person.profilePath),
                noDataImage = R.drawable.no_person_photo,
                modifier = Modifier.padding(5.dp),
                onClick = {
                    onClick(person.id)
                },
                contentDescription = person.name
            )
        }
    }
}

@Composable
fun PosterItem(
    modifier: Modifier = Modifier,
    width: Dp = POSTER_WIDTH,
    height: Dp = POSTER_HEIGHT,
    onClick: (id: Int) -> Unit = {},
    elevation: Dp = 8.dp,
    mediaItem: TmdbItem?
) {
    PosterItem(
        modifier = modifier,
        width = width,
        height = height,
        onClick = {
            mediaItem?.let {
                onClick(mediaItem.id)
            }
        },
        url = mediaItem?.let { TmdbUtils.getFullPosterPath(mediaItem) },
        elevation = elevation,
        contentDescription = mediaItem?.title
    )
}

@Composable
fun PosterItem(
    modifier: Modifier = Modifier,
    width: Dp = POSTER_WIDTH,
    height: Dp = POSTER_HEIGHT,
    onClick: (id: Int) -> Unit = {},
    elevation: Dp = 8.dp,
    person: Person?
) {
    PosterItem(
        modifier = modifier,
        width = width,
        height = height,
        onClick = {
            person?.let {
                onClick(person.id)
            }
        },
        url = person?.let { TmdbUtils.getFullPersonImagePath(person) },
        elevation = 8.dp,
        contentDescription = person?.name
    )
}

@Composable
fun PosterItem(
    url: String?,
    modifier: Modifier = Modifier,
    width: Dp = POSTER_WIDTH,
    height: Dp = POSTER_HEIGHT,
    onClick: () -> Unit = {},
    noDataImage: Int = R.drawable.placeholder,
    placeholder: Int = R.drawable.placeholder,
    elevation: Dp = 8.dp,
    contentDescription: String?
) {
    Card(
        elevation = elevation,
        modifier = modifier
            .size(width = width, height = height),
        shape = RoundedCornerShape(5.dp)
    ) {
        if (url != null) {
            AsyncImage(
                modifier = Modifier
                    .size(width = width, height = height)
                    .clip(RoundedCornerShape(5.dp))
                    .clickable(
                        onClick = onClick
                    ),
                model = url,
                placeholder = rememberAsyncImagePainter(model = placeholder),
                contentDescription = contentDescription,
                contentScale = ContentScale.FillBounds
            )
        } else {
            Image(
                modifier = Modifier
                    .size(width = width, height = height)
                    .clip(RoundedCornerShape(5.dp))
                    .clickable(
                        onClick = onClick
                    ),
                painter = rememberAsyncImagePainter(model = noDataImage),
                contentDescription = contentDescription,
                contentScale = ContentScale.FillBounds
            )
        }
    }
}