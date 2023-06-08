package com.owenlejeune.tvtime.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.owenlejeune.tvtime.extensions.toDp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Gallery(
    pagerState: PagerState,
    models: List<Any?>,
    modifier: Modifier = Modifier,
    contentDescriptions: List<String> = emptyList()
) {
    HorizontalPager(
        count = models.size,
        state = pagerState,
        modifier = modifier
    ) { page ->
        AsyncImage(
            model = models[page],
            contentDescription = contentDescriptions[page],
            contentScale = ContentScale.FillWidth
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TapGallery(
    pagerState: PagerState,
    models: List<Any?>,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    var showControls by remember { mutableStateOf(false) }
    var lastTappedTime by remember { mutableStateOf(System.currentTimeMillis()) }

    var job: Job? = null
    LaunchedEffect(lastTappedTime) {
        if (showControls) {
            job = scope.launch {
                delay(5.seconds)
                withContext(Dispatchers.Main) {
                    showControls = false
                }
            }
        }
    }

    Box(
        modifier = modifier
            .clickable {
                job?.cancel()
                showControls = true
                lastTappedTime = System.currentTimeMillis()
            }
    ) {
        val sizeImage = remember { mutableStateOf(IntSize.Zero) }
        HorizontalPager(
            count = models.size,
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .onGloballyPositioned { sizeImage.value = it.size }
        ) { page ->
            AsyncImage(
                model = models[page],
                contentDescription = null,
                contentScale = ContentScale.FillWidth
            )
        }

        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            val gradient = Brush.horizontalGradient(
                colors = listOf(Color.Black, Color.Transparent),
                startX = 0f,
                endX = (sizeImage.value.width/2).toFloat(),
                tileMode = TileMode.Mirror
            )

            Box(
                modifier = Modifier
                    .background(brush = gradient)
                    .size(
                        width = sizeImage.value.width.toDp(),
                        height = sizeImage.value.height.toDp()
                    )
            ) {
                IconButton(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(width = 48.dp)
                        .align(Alignment.CenterStart),
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ChevronLeft,
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.Center),
                        tint = MaterialTheme.colorScheme.surface
                    )
                }

                IconButton(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(width = 48.dp)
                        .align(Alignment.CenterEnd),
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ChevronRight,
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.Center),
                        tint = MaterialTheme.colorScheme.surface
                    )
                }
//                Box(
//                    modifier = Modifier
//                        .background(brush = leftGradient)
//                        .height(height = sizeImage.value.height.toDp())
//                        .width((sizeImage.value.width/2).toDp())
//                        .align(Alignment.CenterStart)
//                        .onGloballyPositioned { leftSizeImage.value = it.size }
//                        .clickable {
//                            val target =
//                                if (pagerState.currentPage == 0) models.size - 1 else pagerState.currentPage - 1
//                            scope.launch { pagerState.animateScrollToPage(target) }
//                        }
//                ) {
//                    Icon(
//                        imageVector = Icons.Outlined.ChevronLeft,
//                        contentDescription = null,
//                        modifier = Modifier
//                            .size(48.dp)
//                            .padding(start = 24.dp)
//                            .align(Alignment.CenterStart),
//                        tint = MaterialTheme.colorScheme.surface
//                    )
//                }
//                Box(
//                    modifier = Modifier
//                        .background(brush = rightGradient)
//                        .height(height = sizeImage.value.height.toDp())
//                        .width((sizeImage.value.width/2).toDp())
//                        .align(Alignment.CenterEnd)
//                        .onGloballyPositioned { rightSizeImage.value = it.size }
//                        .clickable {
//                            val target =
//                                if (pagerState.currentPage == models.size - 1) 0 else pagerState.currentPage + 1
//                            scope.launch { pagerState.animateScrollToPage(target) }
//                        }
//                ) {
//                    Icon(
//                        imageVector = Icons.Outlined.ChevronRight,
//                        contentDescription = null,
//                        modifier = Modifier
//                            .size(48.dp)
//                            .padding(end = 24.dp)
//                            .align(Alignment.CenterEnd),
//                        tint = MaterialTheme.colorScheme.surface
//                    )
//                }
            }
        }

//        AnimatedVisibility(
//            visible = showControls.value,
//            enter = fadeIn(),
//            exit = fadeOut()
//        ) {
//            val leftSizeImage = remember { mutableStateOf(IntSize.Zero) }
//            val leftGradient = Brush.horizontalGradient(
//                colors = listOf(Color.Black, Color.Transparent),
//                startX = 0f,
//                endX = leftSizeImage.value.width.toFloat()
//            )
//            Box(
//                modifier = Modifier
//                    .background(brush = leftGradient)
//                    .fillMaxHeight()
//                    .width(100.dp)
//                    .align(Alignment.CenterStart)
//                    .onGloballyPositioned { leftSizeImage.value = it.size }
//                    .clickable {
//                        val target =
//                            if (pagerState.currentPage == 0) models.size - 1 else pagerState.currentPage + 1
//                        scope.launch { pagerState.animateScrollToPage(target) }
//                    }
//            ) {
//                Icon(
//                    imageVector = Icons.Outlined.ChevronLeft,
//                    contentDescription = null,
//                    modifier = Modifier.size(48.dp)
//                )
//            }
//
//            val rightSizeImage = remember { mutableStateOf(IntSize.Zero) }
//            val rightGradient = Brush.horizontalGradient(
//                colors = listOf(Color.Black, Color.Transparent),
//                startX = leftSizeImage.value.width.toFloat(),
//                endX = 0f
//            )
//            Box(
//                modifier = Modifier
//                    .background(brush = rightGradient)
//                    .fillMaxHeight()
//                    .width(100.dp)
//                    .align(Alignment.CenterEnd)
//                    .onGloballyPositioned { rightSizeImage.value = it.size }
//                    .clickable {
//                        val target =
//                            if (pagerState.currentPage == models.size - 1) 0 else pagerState.currentPage - 1
//                        scope.launch { pagerState.animateScrollToPage(target) }
//                    }
//            ) {
//                Icon(
//                    imageVector = Icons.Outlined.ChevronRight,
//                    contentDescription = null,
//                    modifier = Modifier.size(48.dp)
//                )
//            }
//        }
    }
}