package com.owenlejeune.tvtime.ui.screens

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
fun DetailContent(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier
        .background(color = MaterialTheme.colorScheme.background)
        .verticalScroll(rememberScrollState())
    ) {
        content()
    }
}

@Composable
fun DetailHeader(
    appNavController: NavController,
    title: String,
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
            backButton, backdropImage, posterImage, titleText, ratingsView
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
                    top.linkTo(backButton.bottom)
                },
            url = posterUrl,
            contentDescription = posterContentDescription
        )

        TitleText(
            modifier = Modifier
                .constrainAs(titleText) {
                    bottom.linkTo(posterImage.bottom)
                    start.linkTo(posterImage.end, margin = 8.dp)
                    end.linkTo(parent.end, margin = 16.dp)
                },
            title = title
        )

        rating?.let {
            RatingView(
                modifier = Modifier
                    .constrainAs(ratingsView) {
                        bottom.linkTo(titleText.top)
                        start.linkTo(posterImage.end, margin = 20.dp)
                    },
                progress = rating
            )
        }

        BackButton(
            modifier = Modifier.constrainAs(backButton) {
                top.linkTo(parent.top)//, 8.dp)
                start.linkTo(parent.start, 8.dp)
                bottom.linkTo(posterImage.top)
            },
            appNavController = appNavController
        )
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
            .height(280.dp),
        imageUrl = TmdbUtils.getFullBackdropPath(imageUrl),
        contentDescription = contentDescription
//            collection = images.value
    )
}

@Composable
private fun TitleText(modifier: Modifier, title: String) {
    Text(
        text = title,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
            .padding(start = 16.dp, end = 16.dp)
            .fillMaxWidth(.6f),
        style = MaterialTheme.typography.headlineMedium,
        textAlign = TextAlign.Start,
        softWrap = true,
        maxLines = 3,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun RatingView(
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

@Composable
private fun BackButton(modifier: Modifier, appNavController: NavController) {
    val start = if (isSystemInDarkTheme()) Color.Black else Color.White
    IconButton(
        onClick = { appNavController.popBackStack() },
        modifier = modifier
            .background(
                brush = Brush.radialGradient(colors = listOf(start, Color.Transparent))
            )
            .wrapContentSize()
    ) {
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = stringResource(R.string.content_description_back_button),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}