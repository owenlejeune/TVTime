package com.owenlejeune.tvtime.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.ui.components.BackdropImage
import com.owenlejeune.tvtime.ui.components.PosterItem
import com.owenlejeune.tvtime.ui.components.RatingRing
import com.owenlejeune.tvtime.utils.TmdbUtils

@Composable
fun DetailHeader(
    modifier: Modifier = Modifier,
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

        Backdrop(
            modifier = Modifier
                .constrainAs(backdropImage) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            imageUrl = backdropUrl,
            contentDescription = backdropContentDescription
        )

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
private fun Backdrop(modifier: Modifier, imageUrl: String?, contentDescription: String? = null) {
//        val images = remember { mutableStateOf<ImageCollection?>(null) }
//        itemId?.let {
//            if (images.value == null) {
//                fetchImages(itemId, service, images)
//            }
//        }
    BackdropImage(
        modifier = modifier
            .fillMaxWidth()
            .height(230.dp),
        imageUrl = TmdbUtils.getFullBackdropPath(imageUrl),
        contentDescription = contentDescription
//            collection = images.value
    )
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