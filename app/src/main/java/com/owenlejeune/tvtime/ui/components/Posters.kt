package com.owenlejeune.tvtime.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.api.tmdb.model.HomePagePerson
import com.owenlejeune.tvtime.api.tmdb.model.ImageCollection
import com.owenlejeune.tvtime.api.tmdb.model.Person
import com.owenlejeune.tvtime.api.tmdb.model.TmdbItem
import com.owenlejeune.tvtime.extensions.dpToPx
import com.owenlejeune.tvtime.extensions.listItems
import com.owenlejeune.tvtime.utils.TmdbUtils

private val POSTER_WIDTH = 120.dp
private val POSTER_HEIGHT = 190.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PosterGrid(
    fetchMedia: (MutableState<List<TmdbItem>>) -> Unit = {},
    onClick: (Int) -> Unit = {}
) {
    val mediaList = remember { mutableStateOf(emptyList<TmdbItem>()) }
    fetchMedia(mediaList)

    LazyVerticalGrid(
        cells = GridCells.Adaptive(minSize = POSTER_WIDTH),
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
        cells = GridCells.Adaptive(minSize = POSTER_WIDTH),
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
    val context = LocalContext.current
    Card(
        elevation = elevation,
        modifier = modifier
            .size(width = width, height = height),
        shape = RoundedCornerShape(5.dp)
    ) {
        Image(
            painter = if (url != null) {
                rememberImagePainter(
                    data = url ?: noDataImage,
                    builder = {
                        transformations(RoundedCornersTransformation(5f.dpToPx(context)))
                        placeholder(placeholder)
                    }
                )
            } else {
                rememberImagePainter(ContextCompat.getDrawable(context, R.drawable.placeholder))
            },
            contentDescription = contentDescription,
            modifier = Modifier
                .size(width = width, height = height)
                .clickable(
                    onClick = onClick
                )
        )
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalPagerApi::class)
@Composable
fun BackdropImage(
    modifier: Modifier = Modifier,
    imageUrl: String? = null,
    collection: ImageCollection? = null,
    contentDescription: String? = null
) {
    val context = LocalContext.current

    var sizeImage by remember { mutableStateOf(IntSize.Zero) }

    val gradient = Brush.verticalGradient(
        colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background),
        startY = sizeImage.height.toFloat() / 3,
        endY = sizeImage.height.toFloat()
    )

    Box(
        modifier = modifier
    ) {
        if (collection != null) {
            val pagerState = rememberPagerState()
            HorizontalPager(count = collection.backdrops.size, state = pagerState) { page ->
                val backdrop = collection.backdrops[page]
                Image(
                    painter = rememberImagePainter(
                        data = TmdbUtils.getFullBackdropPath(backdrop),
                        builder = {
                            placeholder(R.drawable.placeholder)
                        }
                    ),
                    contentDescription = "",
                    modifier = Modifier.onGloballyPositioned {
                        sizeImage = it.size
                    }
                )
            }
        } else {
            Image(
                painter = if (imageUrl != null) {
                    rememberImagePainter(
                        data = imageUrl,
                        builder = {
                            placeholder(R.drawable.placeholder)
                        }
                    )
                } else {
                    rememberImagePainter(ContextCompat.getDrawable(context, R.drawable.placeholder))
                },
                contentDescription = contentDescription,
                modifier = Modifier.onGloballyPositioned {
                    sizeImage = it.size
                }
            )
        }
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(gradient)
        )
    }
}