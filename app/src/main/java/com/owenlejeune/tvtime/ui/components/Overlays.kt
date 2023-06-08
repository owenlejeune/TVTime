package com.owenlejeune.tvtime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.ImageCollection
import com.owenlejeune.tvtime.utils.TmdbUtils

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ImageGalleryOverlay(
    imageCollection: ImageCollection,
    selectedImage: Int,
    onDismissRequest: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black.copy(alpha = 0.7f))
            .clickable(onClick = onDismissRequest)
    ) {
        val pagerState = rememberPagerState(initialPage = selectedImage)
        TapGallery(
            pagerState = pagerState,
            models = imageCollection.backdrops.map { TmdbUtils.getFullBackdropPath(it) },
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.Center)
        )
    }
}