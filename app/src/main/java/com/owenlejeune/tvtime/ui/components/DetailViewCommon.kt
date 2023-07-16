package com.owenlejeune.tvtime.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.api.LoadingState
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.ExternalIds
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.ImageCollection
import com.owenlejeune.tvtime.extensions.shimmerBackground
import com.owenlejeune.tvtime.extensions.toDp
import com.owenlejeune.tvtime.ui.viewmodel.MainViewModel
import com.owenlejeune.tvtime.utils.TmdbUtils
import com.owenlejeune.tvtime.utils.types.MediaViewType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun DetailHeader(
    modifier: Modifier = Modifier,
    showGalleryOverlay: MutableState<Boolean>? = null,
    imageCollection: ImageCollection? = null,
    backdropUrl: String? = null,
    posterUrl: String? = null,
    backdropContentDescription: String? = null,
    posterContentDescription: String? = null,
    rating: Float? = null,
    pagerState: PagerState? = null,
    elevation: Dp = 50.dp
) {
    Box(
        modifier = modifier.then(
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        )
    ) {
        if (imageCollection != null) {
            BackdropGallery(
                modifier = Modifier
                    .clickable {
                        showGalleryOverlay?.value = true
                    },
                imageCollection = imageCollection,
                state = pagerState
            )
        } else {
            Backdrop(
                imageUrl = backdropUrl,
                contentDescription = backdropContentDescription
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            PosterItem(
                url = posterUrl,
                title = posterContentDescription,
                elevation = elevation,
                overrideShowTitle = false,
                enabled = false
            )

            rating?.let {
                if (it > 0f) {
                    RatingView(
                        progress = rating
                    )
                }
            }
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
        startY = 0f,//sizeImage.value.height.toFloat() / 4,
        endY = sizeImage.value.height.toFloat()
    )

    Box(
        modifier = modifier
    ) {
        content(sizeImage)

        Box(
            modifier = Modifier
                .width(sizeImage.value.width.toDp() + 2.dp)
                .height(sizeImage.value.height.toDp() + 4.dp)
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
            val model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .diskCacheKey(imageUrl)
                .networkCachePolicy(CachePolicy.ENABLED)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .build()
            AsyncImage(
                model = model,
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
fun BackdropGallery(
    modifier: Modifier,
    imageCollection: ImageCollection?,
    delayMillis: Long = 5000,
    state: PagerState? = null
) {
    BackdropContainer(
        modifier = modifier
    ) { sizeImage ->
        if (imageCollection != null) {
            val pagerState = state ?: rememberPagerState(initialPage = 0)
            HorizontalPager(
                count = imageCollection.backdrops.size,
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .onGloballyPositioned { sizeImage.value = it.size },
            ) { page ->
                val backdrop = imageCollection.backdrops[page]
                val url = TmdbUtils.getFullBackdropPath(backdrop)
                val model = ImageRequest.Builder(LocalContext.current)
                    .data(url)
                    .diskCacheKey(url ?: "")
                    .networkCachePolicy(CachePolicy.ENABLED)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .build()
                AsyncImage(
                    model = model,
                    placeholder = rememberAsyncImagePainter(model = R.drawable.placeholder),
                    contentDescription = "",
                    contentScale = ContentScale.FillWidth
                )
            }

            // fixes an issue where using pagerState.current page breaks paging animations
            if (delayMillis > 0) {
                var key by remember { mutableStateOf(false) }
                LaunchedEffect(key1 = key) {
                    launch {
                        delay(delayMillis)
                        with(pagerState) {
                            val target = if (currentPage < pageCount - 1) currentPage + 1 else 0
                            animateScrollToPage(target)
                            key = !key
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExternalIdsArea(
    type: MediaViewType,
    itemId: Int,
    modifier: Modifier = Modifier
) {
    val mainViewModel = viewModel<MainViewModel>()
    val loadingState = remember { mainViewModel.produceExternalIdsLoadingStateFor(type) }

    val externalIdsMap = remember { mainViewModel.produceExternalIdsFor(type) }
    val externalIds = externalIdsMap[itemId]

    if (loadingState.value == LoadingState.LOADING || loadingState.value == LoadingState.REFRESHING) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (i in 0 until 4) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(5.dp))
                        .size(28.dp)
                        .shimmerBackground(
                            RoundedCornerShape(5.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                )
            }
        }
    } else if (externalIds?.hasExternalIds() == true) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            externalIds.twitter?.let {
                ExternalIdLogo(url = "https://twitter.com/$it", logoPainter = painterResource(id = R.drawable.twitter_logo))
            }
            externalIds.facebook?.let {
                ExternalIdLogo(url = "https://facebook.com/$it", logoPainter = painterResource(id = R.drawable.facebook_logo))
            }
            externalIds.instagram?.let {
                ExternalIdLogo(url = "https://instagram.com/$it", logoPainter = painterResource(id = R.drawable.instagram_logo))
            }
            externalIds.tiktok?.let {
                ExternalIdLogo(url = "https://tiktok.com/@$it", logoPainter = painterResource(id = R.drawable.tiktok_logo))
            }
            externalIds.youtube?.let {
                ExternalIdLogo(url = "https://youtube.com/$it", logoPainter = painterResource(id = R.drawable.youtube_logo))
            }
        }
    }
}

@Composable
private fun ExternalIdLogo(
    logoPainter: Painter,
    url: String,
) {
    val context = LocalContext.current

    IconButton(
        onClick = {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        },
        modifier = Modifier.size(28.dp)
//        modifier = Modifier.size(40.dp)
    ) {
        Icon(
            painter = logoPainter,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
fun PlaceholderDetailHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .aspectRatio(1.778f)
            .shimmerBackground()
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            PlaceholderPosterItem()

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(60.dp)
                    .shimmerBackground(tint = MaterialTheme.colorScheme.secondary)
            )
        }
    }
}

@Composable
fun AdditionalDetailItem(
    title: String,
    subtext: String,
    includeDivider: Boolean = true
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Text(
            text = subtext,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        if (includeDivider) {
            Divider()
        }
    }
}