package com.owenlejeune.tvtime.ui.screens.main

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.ImageCollection
import com.owenlejeune.tvtime.preferences.AppPreferences
import com.owenlejeune.tvtime.ui.components.PosterItem
import com.owenlejeune.tvtime.ui.components.RatingRing
import com.owenlejeune.tvtime.utils.TmdbUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.get

@Composable
fun DetailHeader(
    modifier: Modifier = Modifier,
    imageCollection: ImageCollection? = null,
    backdropUrl: String? = null,
    posterUrl: String? = null,
    backdropContentDescription: String? = null,
    posterContentDescription: String? = null,
    rating: Float? = null
) {
    ConstraintLayout(modifier = modifier
        .fillMaxWidth()
        .wrapContentHeight()
    ) {
        val (
            backdropImage, posterImage, ratingsView
        )  = createRefs()

        if (imageCollection != null) {
            BackdropGallery(
                modifier = Modifier
                    .constrainAs(backdropImage) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                imageCollection = imageCollection
            )
        } else {
            Backdrop(
                imageUrl = backdropUrl,
                contentDescription = backdropContentDescription
            )
        }

        PosterItem(
            modifier = Modifier
                .constrainAs(posterImage) {
                    bottom.linkTo(backdropImage.bottom)
                    start.linkTo(parent.start, margin = 16.dp)
                },
            url = posterUrl,
            contentDescription = posterContentDescription,
            elevation = 20.dp
        )

        rating?.let {
            RatingView(
                modifier = Modifier
                    .constrainAs(ratingsView) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(posterImage.end, margin = 20.dp)
                    },
                progress = rating
            )
        }
    }
}

@Composable
private fun BackdropContainer(
    modifier: Modifier = Modifier,
    content: @Composable (MutableState<IntSize>) -> Unit
) {
    val sizeImage = remember { mutableStateOf(IntSize.Zero) }

    val gradient = Brush.verticalGradient(
        colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background),
        startY = sizeImage.value.height.toFloat() / 3,
        endY = sizeImage.value.height.toFloat()
    )

    Box(
        modifier = modifier
    ) {
        content(sizeImage)

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(gradient)
        )
    }
}

@Composable
private fun Backdrop(
    modifier: Modifier = Modifier,
    imageUrl: String?,
    contentDescription: String? = null
) {
    BackdropContainer(
        modifier = modifier
    ) { sizeImage ->
        if (imageUrl != null) {
            AsyncImage(
                model = imageUrl,
                placeholder = rememberAsyncImagePainter(model = R.drawable.placeholder),
                contentDescription = contentDescription,
                modifier = Modifier.onGloballyPositioned { sizeImage.value = it.size },
                contentScale = ContentScale.FillWidth
            )
        } else {
            Image(
                painter = rememberAsyncImagePainter(model = R.drawable.placeholder),
                contentDescription = contentDescription,
                modifier = Modifier.onGloballyPositioned { sizeImage.value = it.size },
                contentScale = ContentScale.FillWidth
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun BackdropGallery(
    modifier: Modifier,
    imageCollection: ImageCollection?
) {
    BackdropContainer(
        modifier = modifier
    ) { sizeImage ->
        if (imageCollection != null) {
            val pagerState = rememberPagerState()
            HorizontalPager(count = imageCollection.backdrops.size, state = pagerState) { page ->
                val backdrop = imageCollection.backdrops[page]
                AsyncImage(
                    model = TmdbUtils.getFullBackdropPath(backdrop),
                    placeholder = rememberAsyncImagePainter(model = R.drawable.placeholder),
                    contentDescription = "",
                    modifier = Modifier.onGloballyPositioned { sizeImage.value = it.size }
                )
            }
            
            LaunchedEffect(key1 = pagerState.currentPage) {
                launch {
                    delay(5000)
                    with (pagerState) {
                        val target = if (currentPage < pageCount - 1) currentPage + 1 else 0

                        pagerState.animateScrollToPage(target)
                    }
                }
            }
        }
    }
}

@Composable
fun RatingView(
    progress: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .size(60.dp)
            .background(color = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        RatingRing(
            modifier = Modifier.padding(5.dp),
            textColor = MaterialTheme.colorScheme.onSurfaceVariant,
            progress = progress,
            textSize = 14.sp,
            ringColor = MaterialTheme.colorScheme.primary,
            ringStrokeWidth = 4.dp,
            size = 50.dp
        )
    }
}