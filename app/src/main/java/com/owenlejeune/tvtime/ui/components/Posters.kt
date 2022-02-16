package com.owenlejeune.tvtime.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.owenlejeune.tvtime.utils.TmdbUtils
import com.owenlejeune.tvtime.api.tmdb.model.ImageCollection
import com.owenlejeune.tvtime.api.tmdb.model.TmdbItem
import com.owenlejeune.tvtime.extensions.dpToPx
import com.owenlejeune.tvtime.extensions.listItems

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PosterGrid(
    fetchMedia: (MutableState<List<TmdbItem>>) -> Unit = {},
    onClick: (Int) -> Unit = {}
) {
    val mediaList = remember { mutableStateOf(emptyList<TmdbItem>()) }
    fetchMedia(mediaList)

    LazyVerticalGrid(
        cells = GridCells.Fixed(count = 3),
        contentPadding = PaddingValues(8.dp)
    ) {
        listItems(mediaList.value) { item ->
            PosterItem(
                mediaItem = item,
                onClick = onClick
            )
        }
    }
}

@Composable
fun PosterItem(
    modifier: Modifier = Modifier,
    width: Dp = 127.dp,
    height: Dp = 190.dp,
    onClick: (Int) -> Unit = {},
    mediaItem: TmdbItem?
) {
    val context = LocalContext.current
    val poster = mediaItem?.let { TmdbUtils.getFullPosterPath(mediaItem) }
    Card(
        elevation = 8.dp,
        modifier = modifier
            .size(width = width, height = height)
            .padding(5.dp),
        shape = RoundedCornerShape(5.dp)
    ) {
        Image(
            painter = if (mediaItem != null) {
                rememberImagePainter(
                    data = poster,
                    builder = {
                        transformations(RoundedCornersTransformation(5f.dpToPx(context)))
                        placeholder(R.drawable.placeholder)
                    }
                )
            } else {
                rememberImagePainter(ContextCompat.getDrawable(context, R.drawable.placeholder))
            },
            contentDescription = mediaItem?.title,
            modifier = Modifier
                .size(width = width, height = height)
                .clickable {
                    mediaItem?.let {
                        onClick(mediaItem.id)
                    }
                }
        )
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalPagerApi::class)
@Composable
fun BackdropImage(
    modifier: Modifier = Modifier,
    imageUrl: String? = null,
    collection: ImageCollection? = null
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
                contentDescription = "",
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