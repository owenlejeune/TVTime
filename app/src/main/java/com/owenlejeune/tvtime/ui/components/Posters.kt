package com.owenlejeune.tvtime.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.api.tmdb.TmdbUtils
import com.owenlejeune.tvtime.api.tmdb.model.TmdbItem
import com.owenlejeune.tvtime.extensions.dpToPx
import com.owenlejeune.tvtime.extensions.listItems
import com.owenlejeune.tvtime.ui.navigation.MainNavItem
import com.owenlejeune.tvtime.ui.screens.DetailViewType

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
        modifier = modifier
            .size(width = width, height = height)
            .padding(5.dp)
            .clickable {
                mediaItem?.let {
                    onClick(mediaItem.id)
                }
            }
    )
}