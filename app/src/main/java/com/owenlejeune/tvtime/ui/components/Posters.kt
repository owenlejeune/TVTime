package com.owenlejeune.tvtime.ui.components

import android.widget.Toast
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.api.tmdb.TmdbUtils
import com.owenlejeune.tvtime.api.tmdb.model.MediaItem
import com.owenlejeune.tvtime.extensions.dpToPx
import com.owenlejeune.tvtime.extensions.listItems
import com.owenlejeune.tvtime.ui.navigation.MainNavItem

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PosterGrid(
    appNavController: NavController,
    fetchMedia: (MutableState<List<MediaItem>>) -> Unit
) {
    val mediaList = remember { mutableStateOf(emptyList<MediaItem>()) }
    fetchMedia(mediaList)

    LazyVerticalGrid(
        cells = GridCells.Fixed(count = 3),
        contentPadding = PaddingValues(8.dp)
    ) {
        listItems(mediaList.value) { item ->
            PosterItem(appNavController = appNavController, mediaItem = item)
        }
    }
}

@Composable
fun PosterItem(
    appNavController: NavController,
    mediaItem: MediaItem
) {
    val context = LocalContext.current
    val poster = TmdbUtils.getFullPosterPath(mediaItem)
    Image(
        painter = rememberImagePainter(
            data = poster,
            builder = {
                transformations(RoundedCornersTransformation(5f.dpToPx(context)))
                placeholder(R.drawable.placeholder)
            }
        ),
        contentDescription = mediaItem.title,
        modifier = Modifier
            .size(190.dp)
            .padding(5.dp)
            .clickable {
                appNavController.navigate("${MainNavItem.DetailView.route}/${mediaItem.id}")
//                appNavController.n
//                Toast
//                    .makeText(context, "${mediaItem.title} clicked", Toast.LENGTH_SHORT)
//                    .show()
            }
    )
}