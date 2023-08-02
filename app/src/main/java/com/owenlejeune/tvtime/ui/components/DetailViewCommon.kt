package com.owenlejeune.tvtime.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.api.common.LoadingState
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.Episode
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.EpisodeCastMember
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.EpisodeCrewMember
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.Image
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.ImageCollection
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.MovieCastMember
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.MovieCrewMember
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.Person
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.TvCastMember
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.TvCrewMember
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.Video
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.WatchProviderDetails
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.WatchProviders
import com.owenlejeune.tvtime.extensions.combineWith
import com.owenlejeune.tvtime.extensions.listItems
import com.owenlejeune.tvtime.extensions.shimmerBackground
import com.owenlejeune.tvtime.extensions.toDp
import com.owenlejeune.tvtime.ui.navigation.AppNavItem
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
    expandedPosterAsBackdrop: Boolean = false,
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
        if (expandedPosterAsBackdrop) {
            Box {
                val url = TmdbUtils.getFullPosterPath(posterUrl)
                val model = ImageRequest.Builder(LocalContext.current)
                    .data(url)
                    .diskCacheKey(url ?: "")
                    .networkCachePolicy(CachePolicy.ENABLED)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .build()
                AsyncImage(
                    model = model,
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .blur(radius = 10.dp)
                        .fillMaxWidth()
                        .aspectRatio(1.778f)
                )

                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Black.copy(alpha = 0.6f))
                        .blur(radius = 5.dp)
                )
            }
        } else {
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
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            posterUrl?.let {
                PosterItem(
                    url = posterUrl,
                    title = posterContentDescription,
                    elevation = elevation,
                    overrideShowTitle = false,
                    enabled = false
                )
            }

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

@Composable
fun EpisodeItem(
    seriesId: Int,
    episode: Episode,
    appNavController: NavController,
    elevation: Dp = 10.dp,
    maxDescriptionLines: Int = 2,
    rating: Int? = null
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val codedId = seriesId
                    .combineWith(episode.seasonNumber)
                    .combineWith(episode.episodeNumber)
                appNavController.navigate(
                    AppNavItem.DetailView.withArgs(MediaViewType.EPISODE, codedId)
                )
            }
    ) {
        Box {
            episode.stillPath?.let {
//                Cloudy(
//                    modifier = Modifier.background(Color.Black.copy(alpha = 0.4f))
//                ) {
                val url = TmdbUtils.getFullEpisodeStillPath(it)
                val model = ImageRequest.Builder(LocalContext.current)
                    .data(url)
                    .diskCacheKey(url ?: "")
                    .networkCachePolicy(CachePolicy.ENABLED)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .build()
                AsyncImage(
                    model = model,
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .blur(radius = 10.dp)
//                        .fillMaxWidth()
//                        .wrapContentHeight()
                        .matchParentSize()
                )

                Box(
                    modifier = Modifier
//                        .fillMaxSize()
                        .matchParentSize()
                        .background(Color.Black.copy(alpha = 0.4f))
                        .blur(radius = 5.dp)
                )
//                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(12.dp)
            ) {
                val textColor = episode.stillPath?.let { Color.White } ?: if (isSystemInDarkTheme()) Color.White else Color.Black
                Text(
                    text = "S${episode.seasonNumber}E${episode.episodeNumber} • ${episode.name}",
                    color = textColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                TmdbUtils.convertEpisodeDate(episode.airDate)?.let {
                    Text(
                        text = it,
                        fontStyle = FontStyle.Italic,
                        fontSize = 12.sp,
                        color = textColor
                    )
                }
                Text(
                    text = episode.overview,
                    overflow = TextOverflow.Ellipsis,
                    color = textColor,
                    maxLines = maxDescriptionLines
                )
                rating?.let {
                    Text(
                        text = stringResource(id = R.string.your_rating, rating),
                        color = textColor.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        }
    }
}

@Composable
fun CastCrewCard(
    appNavController: NavController,
    person: Person
) {
    TwoLineImageTextCard(
        title = person.name,
        modifier = Modifier
            .width(124.dp)
            .wrapContentHeight(),
        subtitle = when (person) {
            is MovieCastMember -> person.character
            is MovieCrewMember -> person.job
            is TvCastMember -> {
                val roles = person.roles.joinToString(separator = "/") { it.role }
                val epsCount = person.totalEpisodeCount
                "$roles ($epsCount Eps.)"
            }
            is TvCrewMember -> {
                val roles = person.jobs.joinToString(separator = "/") { it.role }
                val epsCount = person.totalEpisodeCount
                "$roles ($epsCount Eps.)"
            }
            is EpisodeCastMember -> {
                person.character
            }
            is EpisodeCrewMember -> {
                person.job
            }
            else -> null
        },
        imageUrl = TmdbUtils.getFullPersonImagePath(person),
        titleTextColor = MaterialTheme.colorScheme.onPrimary,
        subtitleTextColor = MaterialTheme.colorScheme.onSecondary,
        onItemClicked = {
            appNavController.navigate(
                AppNavItem.DetailView.withArgs(MediaViewType.PERSON, person.id)
            )
        }
    )
}

@Composable
fun WatchProvidersCard(
    modifier: Modifier = Modifier,
    providers: WatchProviders
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Text(
            text = stringResource(R.string.watch_providers_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(start = 16.dp, top = 12.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        val itemsMap = mutableMapOf<Int, List<WatchProviderDetails>>().apply {
            providers.flaterate?.let { put(0, it) }
            providers.rent?.let { put(1, it) }
            providers.buy?.let { put(2, it) }
        }
        val selected = remember { mutableStateOf(if (itemsMap.isEmpty()) null else itemsMap.values.first()) }

        val context = LocalContext.current
        PillSegmentedControl(
            items = itemsMap.values.toList(),
            itemLabel = { i, _ ->
                when (i) {
                    0 -> context.getString(R.string.streaming_label)
                    1 -> context.getString(R.string.rent_label)
                    2 -> context.getString(R.string.buy_label)
                    else -> ""
                }
            },
            onItemSelected = { i, _ -> selected.value = itemsMap.values.toList()[i] },
            modifier = Modifier.padding(all = 8.dp)
        )

        Crossfade(
            modifier = modifier.padding(top = 4.dp, bottom = 12.dp),
            targetState = selected.value,
            label = ""
        ) { value ->
            WatchProviderContainer(watchProviders = value!!, link = providers.link)
        }

        Row(
            modifier = Modifier.padding(start = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(id = R.string.powered_by),
                fontSize = 12.sp
            )

            Image(
                painter = painterResource(id = R.drawable.justwatch_logo_large),
                contentDescription = null,
                modifier = Modifier.height(14.dp)
            )
        }
    }
}

@Composable
fun WatchProviderContainer(
    watchProviders: List<WatchProviderDetails>,
    link: String
) {
    val context = LocalContext.current
    com.google.accompanist.flowlayout.FlowRow(
        modifier = Modifier.padding(horizontal = 16.dp),
        mainAxisSpacing = 8.dp,
        crossAxisSpacing = 4.dp
    ) {
        watchProviders
            .sortedBy { it.displayPriority }
            .forEach { item ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                            context.startActivity(intent)
                        }
                ) {
                    val url = TmdbUtils.fullLogoPath(item.logoPath)
                    val model = ImageRequest.Builder(LocalContext.current)
                        .data(url)
                        .diskCacheKey(url)
                        .networkCachePolicy(CachePolicy.ENABLED)
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .build()
                    AsyncImage(
                        model = model,
                        contentDescription = null,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                    Text(
                        text = item.providerName,
                        fontSize = 10.sp,
                        modifier = Modifier.width(48.dp),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                }
            }
    }
}

@Composable
fun VideosCard(
    modifier: Modifier = Modifier,
    videos: List<Video>
) {
    ExpandableContentCard(
        modifier = modifier,
        title = {
            Text(
                text = stringResource(id = R.string.videos_label),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 12.dp, top = 8.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        toggleTextColor = MaterialTheme.colorScheme.primary
    ) { isExpanded ->
        VideoGroup(
            results = videos,
            type = Video.Type.TRAILER,
            title = stringResource(id = Video.Type.TRAILER.stringRes)
        )

        if (isExpanded) {
            Video.Type.values().filter { it != Video.Type.TRAILER }.forEach { type ->
                VideoGroup(
                    results = videos,
                    type = type,
                    title = stringResource(id = type.stringRes)
                )
            }
        }
    }
}

@Composable
fun VideoGroup(
    results: List<Video>,
    type: Video.Type,
    title: String
) {
    val videos = results.filter { it.isOfficial && it.type == type }
    if (videos.isNotEmpty()) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 12.dp, top = 8.dp)
        )

        val posterWidth = 120.dp
        LazyRow(modifier = Modifier
            .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            item {
                Spacer(modifier = Modifier.width(8.dp))
            }
            listItems(videos) { video ->
                FullScreenThumbnailVideoPlayer(
                    key = video.key,
                    title = video.name,
                    modifier = Modifier
                        .width(posterWidth)
                        .wrapContentHeight()
                )
            }
            item {
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@Composable
fun ImagesCard(
    images: List<Image>,
    onSeeAll: (() -> Unit)? = null
) {
    ContentCard(
        title = stringResource(R.string.images_title)
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Spacer(modifier = Modifier.width(8.dp))
            }
            items(images) { image ->
                PosterItem(
                    width = 120.dp,
                    url = TmdbUtils.getFullPersonImagePath(image.filePath),
                    placeholder = Icons.Filled.Person,
                    title = ""
                )
            }
            item {
                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        onSeeAll?.let {
            Text(
                text = stringResource(id = R.string.expand_see_all),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(start = 16.dp, bottom = 16.dp)
                    .clickable(onClick = onSeeAll)
            )
        }
    }
}

@Composable
fun CastCard(
    title: String,
    isLoading: Boolean,
    cast: List<Person>?,
    appNavController: NavController,
    onSeeMore: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = MaterialTheme.colorScheme.background
) {
    ContentCard(
        modifier = modifier,
        title = title,
        backgroundColor = backgroundColor,
        textColor = textColor
    ) {
        Column {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                item { Spacer(modifier = Modifier.width(8.dp)) }
                if (isLoading) {
                    items(5) {
                        PlaceholderPosterItem()
                    }
                } else {
                    items(cast?.size ?: 0) { i ->
                        cast?.get(i)?.let {
                            CastCrewCard(appNavController = appNavController, person = it)
                        }
                    }
                }
                item { Spacer(modifier = Modifier.width(8.dp)) }
            }

            onSeeMore?.let {
                if (isLoading) {
                    Text(
                        text = "",
                        modifier = Modifier
                            .padding(start = 12.dp, bottom = 12.dp)
                            .width(80.dp)
                            .shimmerBackground(RoundedCornerShape(10.dp))
                    )
                } else {
                    Text(
                        text = stringResource(R.string.see_all_cast_and_crew),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.inversePrimary,
                        modifier = Modifier
                            .padding(start = 12.dp, bottom = 12.dp)
                            .clickable(onClick = onSeeMore)
                    )
                }
            }
        }
    }
}