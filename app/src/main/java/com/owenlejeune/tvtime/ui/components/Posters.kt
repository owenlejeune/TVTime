package com.owenlejeune.tvtime.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.owenlejeune.tvtime.api.LoadingState
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.HomePagePerson
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.TmdbItem
import com.owenlejeune.tvtime.extensions.header
import com.owenlejeune.tvtime.extensions.lazyPagingItems
import com.owenlejeune.tvtime.extensions.shimmerBackground
import com.owenlejeune.tvtime.preferences.AppPreferences
import com.owenlejeune.tvtime.utils.TmdbUtils
import org.koin.java.KoinJavaComponent.get

private val POSTER_WIDTH = 120.dp
private val POSTER_HEIGHT = 180.dp

@Composable
fun PagingPosterGrid(
    modifier: Modifier = Modifier,
    lazyPagingItems: LazyPagingItems<TmdbItem>?,
    headerContent: @Composable (LazyGridItemScope.() -> Unit)? = null,
    onClick: (id: Int) -> Unit = {}
) {
    lazyPagingItems?.let {
        LazyVerticalGrid(
            modifier = modifier,
            columns = GridCells.Adaptive(minSize = POSTER_WIDTH),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            headerContent?.let {
                header(headerContent)
            }
            lazyPagingItems(
                lazyPagingItems = lazyPagingItems,
                key = { i -> it[i]!!.id }
            ) { item ->
                item?.let {
                    PosterItem(
                        modifier = Modifier.padding(5.dp),
                        mediaItem = item,
                        onClick = onClick
                    )
                }
            }
            lazyPagingItems.apply {
                when {
                    loadState.refresh is LoadState.Loading -> {}
                    loadState.append is LoadState.Loading -> {}
                    loadState.append is LoadState.Error -> {}
                }
            }
        }
    }
}

@Composable
fun PagingPeoplePosterGrid(
    lazyPagingItems: LazyPagingItems<HomePagePerson>?,
    header: @Composable () -> Unit = {},
    onClick: (id: Int) -> Unit = {}
) {
    lazyPagingItems?.let {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = POSTER_WIDTH),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            header {
                header()
            }

            lazyPagingItems(
                lazyPagingItems = lazyPagingItems,
                key = { i -> it[i]!!.id }
            ) { person ->
                person?.let {
                    PosterItem(
                        url = TmdbUtils.getFullPersonImagePath(person.profilePath),
                        placeholder = Icons.Filled.Person,
                        modifier = Modifier.padding(5.dp),
                        onClick = {
                            onClick(person.id)
                        },
                        title = person.name
                    )
                }
            }
            lazyPagingItems.apply {
                when {
                    loadState.refresh is LoadState.Loading -> {}
                    loadState.append is LoadState.Loading -> {}
                    loadState.append is LoadState.Error -> {}
                }
            }
        }
    }
}

@Composable
fun PosterItem(
    modifier: Modifier = Modifier,
    width: Dp = POSTER_WIDTH,
    onClick: (id: Int) -> Unit = {},
    elevation: Dp = 8.dp,
    mediaItem: TmdbItem?
) {
    PosterItem(
        modifier = modifier,
        width = width,
        onClick = {
            mediaItem?.let {
                onClick(mediaItem.id)
            }
        },
        url = mediaItem?.let { TmdbUtils.getFullPosterPath(mediaItem) },
        elevation = elevation,
        title = mediaItem?.title
    )
}

@Composable
fun PosterItem(
    url: String?,
    modifier: Modifier = Modifier,
    width: Dp = POSTER_WIDTH,
    height: Dp = POSTER_HEIGHT,
    onClick: () -> Unit = {},
    enabled: Boolean = true,
    placeholder: ImageVector = Icons.Filled.Movie,
    elevation: Dp = 8.dp,
    title: String?,
    overrideShowTitle: Boolean? = null,
    preferences: AppPreferences = get(AppPreferences::class.java)
) {
    var sizeImage by remember { mutableStateOf(IntSize.Zero) }
    Card(
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = elevation),
        modifier = modifier.then(Modifier
            .width(width = width)
            .height(height = height)
            .wrapContentHeight()
            .clickable(
                enabled = enabled,
                onClick = onClick
            )
        ),
        shape = RoundedCornerShape(5.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        var backgroundColor by remember { mutableStateOf(Color.Gray) }
        val m = if (backgroundColor == Color.Transparent) {
            Modifier.wrapContentHeight()
        } else {
            Modifier.height(height)
        }
        Box(
            modifier = m
                .width(width = width)
                .background(color = backgroundColor)
                .clip(RoundedCornerShape(5.dp))
        ) {
            var bgIcon by remember { mutableStateOf(placeholder) }
            Icon(
                imageVector = bgIcon,
                contentDescription = null,
                modifier = Modifier
                    .focusable(enabled = false)
                    .size(36.dp)
                    .align(Alignment.Center),
                tint = MaterialTheme.colorScheme.background
            )

            val gradient = Brush.verticalGradient(
                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                startY = sizeImage.height.toFloat() / 3f,
                endY = sizeImage.height.toFloat()
            )

            val model = ImageRequest.Builder(LocalContext.current)
                .data(url)
                .diskCacheKey(url ?: "")
                .networkCachePolicy(CachePolicy.ENABLED)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .build()
            AsyncImage(
                modifier = Modifier
                    .width(width = width)
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(5.dp))
                    .onGloballyPositioned { sizeImage = it.size },
                onError = {
                    if (url != null) {
                        bgIcon = Icons.Filled.BrokenImage
                        Log.d("Poster", "Error loading: $url")
                    }
                },
                model = model,
                contentDescription = title,
                contentScale = ContentScale.FillBounds,
                onSuccess = { backgroundColor = Color.Transparent }
            )

            val showTitle = overrideShowTitle ?: preferences.showPosterTitles
            if (showTitle) {
                title?.let {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(5.dp))
                            .background(brush = gradient)
                            .matchParentSize()
                    )

                    Text(
                        text = title,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(6.dp),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}

@Composable
fun PlaceholderPosterItem(
    modifier: Modifier = Modifier,
    width: Dp = POSTER_WIDTH,
    height: Dp = POSTER_HEIGHT,
    tint: Color = Color.LightGray
) {
    Box(
        modifier = modifier.then(Modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(5.dp))
            .shimmerBackground(RoundedCornerShape(5.dp), tint)
        )
    )
}