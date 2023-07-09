package com.owenlejeune.tvtime.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.*
import com.owenlejeune.tvtime.extensions.WindowSizeClass
import com.owenlejeune.tvtime.extensions.getCalendarYear
import com.owenlejeune.tvtime.extensions.lazyPagingItems
import com.owenlejeune.tvtime.extensions.listItems
import com.owenlejeune.tvtime.ui.components.*
import com.owenlejeune.tvtime.ui.navigation.AppNavItem
import com.owenlejeune.tvtime.ui.viewmodel.MainViewModel
import com.owenlejeune.tvtime.utils.SessionManager
import com.owenlejeune.tvtime.utils.TmdbUtils
import com.owenlejeune.tvtime.utils.types.MediaViewType
import kotlinx.coroutines.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun MediaDetailScreen(
    appNavController: NavController,
    itemId: Int,
    type: MediaViewType,
    windowSize: WindowSizeClass
) {
    val mainViewModel = viewModel<MainViewModel>()

    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(color = MaterialTheme.colorScheme.background)
    systemUiController.setNavigationBarColor(color = MaterialTheme.colorScheme.background)

    LaunchedEffect(Unit) {
        mainViewModel.getById(itemId, type)
        mainViewModel.getImages(itemId, type)
    }

    val mediaItems: Map<Int, DetailedItem> = remember { mainViewModel.produceDetailsFor(type) }
    val mediaItem = mediaItems[itemId]

    val imagesMap = remember { mainViewModel.produceImagesFor(type) }
    val images = imagesMap[itemId]

    val topAppBarScrollState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarScrollState)

    val pagerState = rememberPagerState(initialPage = 0)

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        val showGalleryOverlay = remember { mutableStateOf(false) }
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    scrollBehavior = scrollBehavior,
                    colors = TopAppBarDefaults
                        .topAppBarColors(
                            scrolledContainerColor = MaterialTheme.colorScheme.background,
                            titleContentColor = MaterialTheme.colorScheme.primary
                        ),
                    title = { Text(text = mediaItem?.title ?: "") },
                    navigationIcon = {
                        IconButton(
                            onClick = { appNavController.popBackStack() }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = stringResource(id = R.string.content_description_back_button),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                MediaViewContent(
                    appNavController = appNavController,
                    itemId = itemId,
                    mediaItem = mediaItem,
                    images = images,
                    type = type,
                    windowSize = windowSize,
                    showImageGallery = showGalleryOverlay,
                    pagerState = pagerState,
                    mainViewModel =  mainViewModel
                )
            }
        }

        if (showGalleryOverlay.value) {
            images?.let {
                ImageGalleryOverlay(
                    imageCollection = it,
                    selectedImage = pagerState.currentPage,
                    onDismissRequest = { showGalleryOverlay.value = false }
                )
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun MediaViewContent(
    appNavController: NavController,
    mainViewModel: MainViewModel,
    itemId: Int,
    mediaItem: DetailedItem?,
    images: ImageCollection?,
    type: MediaViewType,
    windowSize: WindowSizeClass,
    showImageGallery: MutableState<Boolean>,
    pagerState: PagerState
) {
    LaunchedEffect(Unit) {
        mainViewModel.getExternalIds(itemId, type)
    }

    Row(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.background)
                .weight(1f)
                .verticalScroll(state = rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DetailHeader(
                posterUrl = TmdbUtils.getFullPosterPath(mediaItem?.posterPath),
                posterContentDescription = mediaItem?.title,
                backdropUrl = TmdbUtils.getFullBackdropPath(mediaItem?.backdropPath),
                rating = mediaItem?.voteAverage?.let { it / 10 },
                imageCollection = images,
                showGalleryOverlay = showImageGallery,
                pagerState = pagerState
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DetailsFor(
                    type = type,
                    mediaItem = mediaItem,
                    mainViewModel = mainViewModel,
                    itemId =  itemId
                )

                val externalIdsMap = remember { mainViewModel.produceExternalIdsFor(type) }
                externalIdsMap[itemId]?.let {
                    ExternalIdsArea(
                        externalIds = it,
                        modifier = Modifier.padding(start = 20.dp)
                    )
                }

                val currentSession = remember { SessionManager.currentSession }
                currentSession.value?.let {
                    ActionsView(itemId = itemId, type = type, modifier = Modifier.padding(start = 20.dp))
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    OverviewCard(
                        itemId = itemId,
                        mediaItem = mediaItem,
                        type = type,
                        mainViewModel = mainViewModel,
                        appNavController = appNavController
                    )

                    CastCard(
                        itemId = itemId,
                        appNavController = appNavController,
                        type = type,
                        mainViewModel = mainViewModel
                    )

                    if (type == MediaViewType.TV) {
                        SeasonCard(
                            itemId = itemId,
                            mediaItem = mediaItem,
                            mainViewModel = mainViewModel,
                            appNavController = appNavController
                        )
                    }

                    SimilarContentCard(
                        itemId = itemId,
                        mediaType = type,
                        appNavController = appNavController,
                        mainViewModel = mainViewModel
                    )

                    VideosCard(
                        itemId = itemId,
                        modifier = Modifier.fillMaxWidth(),
                        mainViewModel = mainViewModel,
                        type = type
                    )

                    AdditionalDetailsCard(mediaItem = mediaItem, type = type)

                    WatchProvidersCard(itemId = itemId, type = type, mainViewModel = mainViewModel)

                    if (windowSize != WindowSizeClass.Expanded) {
                        ReviewsCard(itemId = itemId, type = type, mainViewModel = mainViewModel)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (windowSize == WindowSizeClass.Expanded) {
            Column(
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.background)
                    .weight(1f)
                    .verticalScroll(state = rememberScrollState())
            ) {
                ReviewsCard(itemId = itemId, type = type, mainViewModel = mainViewModel)

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun MiscTvDetails(
    itemId: Int,
    mediaItem: DetailedItem?,
    mainViewModel: MainViewModel
) {
    mediaItem?.let { tv ->
        LaunchedEffect(Unit) {
            mainViewModel.getContentRatings(itemId)
        }
        val series = tv as DetailedTv

        val contentRatingsMap = remember { mainViewModel.tvContentRatings }
        val contentRating = TmdbUtils.getTvRating(contentRatingsMap[itemId])

        MiscDetails(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 16.dp),
            year = TmdbUtils.getSeriesRun(series),
            runtime = TmdbUtils.convertRuntimeToHoursMinutes(series),
            genres = series.genres,
            contentRating = contentRating
        )
    }
}

@Composable
private fun MiscMovieDetails(
    itemId: Int,
    mediaItem: DetailedItem?,
    mainViewModel: MainViewModel
) {
    mediaItem?.let { mi ->
        LaunchedEffect(Unit) {
            mainViewModel.getReleaseDates(itemId)
        }

        val movie = mi as DetailedMovie

        val contentRatingsMap = remember { mainViewModel.movieReleaseDates }
        val contentRating = TmdbUtils.getMovieRating(contentRatingsMap[itemId])

        MiscDetails(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 16.dp),
            year = movie.releaseDate?.getCalendarYear()?.toString() ?: "",
            runtime = TmdbUtils.convertRuntimeToHoursMinutes(movie),
            genres = movie.genres,
            contentRating = contentRating
        )
    }
}

@Composable
private fun MiscDetails(
    modifier: Modifier = Modifier,
    year: String,
    runtime: String,
    genres: List<Genre>,
    contentRating: String
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Text(text = year, color = MaterialTheme.colorScheme.onBackground)
            Text(
                text = runtime,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 12.dp)
            )
            Text(
                text = contentRating,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 12.dp)
            )
        }

        ChipGroup(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            chips = genres.map { ChipInfo(it.name, false) }
        )
    }
}

@Composable
private fun OverviewCard(
    itemId: Int,
    mediaItem: DetailedItem?,
    type: MediaViewType,
    mainViewModel: MainViewModel,
    appNavController: NavController,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        mainViewModel.getKeywords(itemId, type)
    }

    val keywordsMap = remember { mainViewModel.produceKeywordsFor(type) }
    val keywords = keywordsMap[itemId]

    mediaItem?.let { mi ->
        if (!mi.tagline.isNullOrEmpty() || keywords?.isNotEmpty() == true || !mi.overview.isNullOrEmpty()) {
            ContentCard(
                modifier = modifier
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    mi.tagline?.let { tagline ->
                        if (tagline.isNotEmpty()) {
                            Text(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                text = tagline,
                                color = MaterialTheme.colorScheme.tertiary
                                ,
                                style = MaterialTheme.typography.bodyLarge,
                                fontStyle = FontStyle.Italic,
                            )
                        }
                    }
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = mi.overview ?: "",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )


                    keywords?.let { keywords ->
                        val keywordsChipInfo = keywords.map { ChipInfo(it.name, true, it.id) }
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(ChipStyle.Rounded.mainAxisSpacing)
                        ) {
                            Spacer(modifier = Modifier.width(8.dp))
                            keywordsChipInfo.forEach { keywordChipInfo ->
                                RoundedChip(
                                    chipInfo = keywordChipInfo,
                                    colors = ChipDefaults.roundedChipColors(
                                        unselectedContainerColor = MaterialTheme.colorScheme.primary,
                                        unselectedContentColor = MaterialTheme.colorScheme.primary
                                    ),
                                    onSelectionChanged = { chip ->
                                        appNavController.navigate(AppNavItem.KeywordsView.withArgs(type, chip.text, chip.id!!))
                                    }
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun AdditionalDetailsCard(
    modifier: Modifier = Modifier,
    mediaItem: DetailedItem?,
    type: MediaViewType
) {
    mediaItem?.let { mi ->
        ContentCard(
            modifier = modifier,
            title = stringResource(R.string.additional_details_title)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AdditionalDetailItem(
                    title = stringResource(R.string.spoken_languages_title),
                    subtext = mi.spokenLanguages.map { it.name }.filter { it.isNotEmpty() }.joinToString(separator = ", ")//joinToString(separator = ", ") { it.name }
                )
                AdditionalDetailItem(
                    title = stringResource(R.string.original_title_title),
                    subtext = mi.originalTitle
                )
                AdditionalDetailItem(
                    title = stringResource(R.string.production_companies_title),
                    subtext = mi.productionCompanies.joinToString(separator = ", ") { it.name }
                )
                AdditionalDetailItem(
                    title = stringResource(R.string.production_countries_title),
                    subtext = mi.productionCountries.joinToString(separator = ", ") { it.name },
                )
                if (type == MediaViewType.MOVIE) {
                    AdditionalMovieItems(movie = mi as DetailedMovie)
                } else {
                    AdditionalTvItems(tv = mi as DetailedTv)
                }
            }
        }
    }
}

@Composable
private fun AdditionalMovieItems(
    movie: DetailedMovie
) {
    AdditionalDetailItem(
        title = stringResource(R.string.movie_budget_title),
        subtext = "$${"%,d".format(movie.budget)}"
    )
    AdditionalDetailItem(
        title = stringResource(R.string.movie_revenue_title),
        subtext = "$${"%,d".format(movie.revenue)}",
        includeDivider = movie.collection != null
    )
    movie.collection?.let {
        AdditionalDetailItem(
            title = stringResource(R.string.movie_collection_title),
            subtext = movie.collection.name,
            includeDivider = false
        )
    }
}

@Composable
private fun AdditionalTvItems(
    tv: DetailedTv
) {
    AdditionalDetailItem(
        title = stringResource(R.string.created_by_title),
        subtext = tv.createdBy.joinToString(separator = ", ") { it.name }
    )
    AdditionalDetailItem(
        title = stringResource(R.string.in_production_title),
        subtext = if (tv.inProduction) {
            stringResource(R.string.in_production_state)
        } else {
            stringResource(R.string.ended_state)
        }
    )
    AdditionalDetailItem(
        title = stringResource(R.string.networks_title),
        subtext = tv.networks.joinToString(separator = ", ") { it.name }
    )
    AdditionalDetailItem(
        title = stringResource(R.string.number_of_episodes_title),
        subtext = tv.numberOfEpisodes.toString()
    )
    AdditionalDetailItem(
        title = stringResource(R.string.number_of_seasons_title),
        subtext = tv.numberOfSeasons.toString()
    )
    AdditionalDetailItem(
        title = stringResource(R.string.last_episode_to_air_title),
        subtext = "#${tv.lastEpisodeToAir.episodeNumber} ${tv.lastEpisodeToAir.name}"
    )
    AdditionalDetailItem(
        title = stringResource(R.string.original_country_title),
        subtext = tv.originCountry.joinToString(separator = ", ")
    )
    AdditionalDetailItem(
        title = stringResource(R.string.tv_type_title),
        subtext = tv.type,
        includeDivider = false
    )
}

@Composable
private fun AdditionalDetailItem(
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
private fun CastCard(
    itemId: Int,
    type: MediaViewType,
    mainViewModel: MainViewModel,
    appNavController: NavController,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        mainViewModel.getCastAndCrew(itemId, type)
    }

    val castMap = remember { mainViewModel.produceCastFor(type) }
    val cast = castMap[itemId]

    ContentCard(
        modifier = modifier,
        title = stringResource(R.string.cast_label),
        backgroundColor = MaterialTheme.colorScheme.primary,
        textColor = MaterialTheme.colorScheme.background
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            item {
                Spacer(modifier = Modifier.width(8.dp))
            }
            items(cast?.size ?: 0) { i ->
                cast?.get(i)?.let {
                    CastCrewCard(appNavController = appNavController, person = it)
                }
            }
            item {
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@Composable
private fun CastCrewCard(appNavController: NavController, person: Person) {
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
private fun SeasonCard(
    itemId: Int,
    mediaItem: DetailedItem?,
    mainViewModel: MainViewModel,
    appNavController: NavController
) {
    LaunchedEffect(mediaItem) {
        val lastSeason = (mediaItem as DetailedTv?)?.numberOfSeasons ?: 0
        if (lastSeason > 0) {
            mainViewModel.getSeason(itemId, lastSeason)
        }
    }

    val seasonsMap = remember { mainViewModel.tvSeasons }
    val lastSeason = seasonsMap[itemId]?.lastOrNull()

    lastSeason?.let {
        ContentCard(
            title = stringResource(R.string.latest_season_title)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(all = 12.dp)
            ) {
                PosterItem(
                    url = TmdbUtils.getFullPosterPath(it.posterPath),
                    title = it.name,
                    overrideShowTitle = false,
                    enabled = false
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = it.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = stringResource(id = R.string.season_ep_count, it.airDate?.getCalendarYear() ?: 0, it.episodes.size)
                    )
                    Text(
                        text = it.overview,
                        fontSize = 14.sp,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Box(
                modifier = Modifier
                    .padding(start = 12.dp, bottom = 12.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable {
                        appNavController.navigate(AppNavItem.SeasonListView.withArgs(id = itemId))
                    }
            ) {
                Text(
                    text = stringResource(id = R.string.see_all_seasons_text),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}

@Composable
fun SimilarContentCard(
    itemId: Int,
    mediaType: MediaViewType,
    mainViewModel: MainViewModel,
    appNavController: NavController,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        mainViewModel.getSimilar(itemId, mediaType)
    }

    val similarContentMap = remember { mainViewModel.produceSimilarContentFor(mediaType) }
    val similarContent = similarContentMap[itemId]
    val pagingItems = similarContent?.collectAsLazyPagingItems()

    pagingItems?.let {
        ContentCard(
            modifier = modifier,
            title = stringResource(id = R.string.recommended_label)
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.width(8.dp))
                }
                lazyPagingItems(pagingItems) { item ->
                    item?.let {
                        TwoLineImageTextCard(
                            title = item.title,
                            modifier = Modifier
                                .width(124.dp)
                                .wrapContentHeight(),
                            imageUrl = TmdbUtils.getFullPosterPath(item),
                            onItemClicked = {
                                appNavController.navigate(
                                    AppNavItem.DetailView.withArgs(mediaType, item.id)
                                )
                            },
                            placeholder = Icons.Filled.Movie,
                            hideSubtitle = true
                        )
                    }
                }
                item {
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
    }
}

@Composable
fun VideosCard(
    itemId: Int,
    type: MediaViewType,
    mainViewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        mainViewModel.getVideos(itemId, type)
    }

    val videosMap = remember { mainViewModel.produceVideosFor(type) }
    val videos = videosMap[itemId]

    if (videos?.any { it.isOfficial } == true) {
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
}

@Composable
private fun VideoGroup(results: List<Video>, type: Video.Type, title: String) {
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
private fun WatchProvidersCard(
    itemId: Int,
    type: MediaViewType,
    mainViewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        mainViewModel.getWatchProviders(itemId, type)
    }

    val watchProvidersMap = remember { mainViewModel.produceWatchProvidersFor(type) }
    val watchProviders = watchProvidersMap[itemId]
    watchProviders?.let { providers ->
        if (providers.buy?.isNotEmpty() == true || providers.rent?.isNotEmpty() == true || providers.flaterate?.isNotEmpty() == true) {
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

                var selectedIndex by remember { mutableIntStateOf(
                    when {
                        providers.flaterate != null -> 0
                        providers.rent != null -> 1
                        providers.buy != null -> 2
                        else -> -1
                    }
                ) }
                Row(
                    modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    providers.flaterate?.let {
                        SelectableTextChip(
                            selected = selectedIndex == 0,
                            onSelected = { selectedIndex = 0 },
                            text = stringResource(R.string.streaming_label)
                        )
                    }
                    providers.rent?.let {
                        SelectableTextChip(
                            selected = selectedIndex == 1,
                            onSelected = { selectedIndex = 1 },
                            text = stringResource(R.string.rent_label)
                        )
                    }
                    providers.buy?.let {
                        SelectableTextChip(
                            selected = selectedIndex == 2,
                            onSelected = { selectedIndex = 2 },
                            text = stringResource(R.string.buy_label)
                        )
                    }
                }
                Crossfade(
                    modifier = modifier.padding(top = 4.dp, bottom = 12.dp),
                    targetState = selectedIndex
                ) { index ->
                    when (index) {
                        0 -> WatchProviderContainer(watchProviders = providers.flaterate!!, link = providers.link)
                        1 -> WatchProviderContainer(watchProviders = providers.rent!!, link = providers.link)
                        2 -> WatchProviderContainer(watchProviders = providers.buy!!, link = providers.link)
                    }
                }
            }
        }
    }
}

@Composable
private fun WatchProviderContainer(
    watchProviders: List<WatchProviderDetails>,
    link: String
) {
    val context = LocalContext.current
    FlowRow(
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
                        .diskCacheKey(url ?: "")
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
private fun ReviewsCard(
    itemId: Int,
    type: MediaViewType,
    mainViewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        mainViewModel.getReviews(itemId, type)
    }

    val reviewsMap = remember { mainViewModel.produceReviewsFor(type) }
    val reviews = reviewsMap[itemId]

   ListContentCard(
       modifier = modifier,
       header = {
           Column(
               verticalArrangement = Arrangement.spacedBy(9.dp)
           ) {
               Text(
                   text = stringResource(R.string.reviews_title),
                   style = MaterialTheme.typography.titleLarge,
                   color = MaterialTheme.colorScheme.onSurfaceVariant
               )

               if (SessionManager.currentSession.value?.isAuthorized == true) {
                   Row(
                       modifier = Modifier
                           .fillMaxWidth()
                           .height(50.dp)
                           .padding(bottom = 4.dp),
                       horizontalArrangement = Arrangement.spacedBy(8.dp)
                   ) {
                       var reviewTextState by remember { mutableStateOf("") }

                       RoundedTextField(
                           modifier = Modifier
                               .height(40.dp)
                               .align(Alignment.CenterVertically)
                               .weight(1f),
                           value = reviewTextState,
                           onValueChange = { reviewTextState = it },
                           placeHolder = stringResource(R.string.add_a_review_hint),
                           backgroundColor = MaterialTheme.colorScheme.secondary,
                           placeHolderTextColor = MaterialTheme.colorScheme.background,
                           textColor = MaterialTheme.colorScheme.onSecondary
                       )

                       val context = LocalContext.current
                       CircleBackgroundColorImage(
                           modifier = Modifier
                               .align(Alignment.CenterVertically)
                               .clickable(
                                   onClick = {
                                       Toast
                                           .makeText(context, "TODO", Toast.LENGTH_SHORT)
                                           .show()
                                   }
                               ),
                           size = 40.dp,
                           backgroundColor = MaterialTheme.colorScheme.tertiary,
                           image = Icons.Filled.Send,
                           colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.surfaceVariant),
                           contentDescription = ""
                       )
                   }
               }
           }
       },
   ) {
       if (reviews?.isNotEmpty() == true) {
           reviews.reversed().forEachIndexed { index, review ->
               Row(
                   modifier = Modifier
                       .fillMaxWidth()
                       .padding(end = 16.dp),
                   verticalAlignment = Alignment.Top,
                   horizontalArrangement = Arrangement.spacedBy(16.dp)
               ) {
                   AvatarImage(
                       size = 50.dp,
                       author = review.authorDetails
                   )

                   Column(
                       verticalArrangement = Arrangement.spacedBy(4.dp)
                   ) {
                       Text(
                           text = review.author,
                           color = MaterialTheme.colorScheme.secondary,
                           fontWeight = FontWeight.Bold
                       )

                       HtmlText(
                           text = review.content,
                           color = MaterialTheme.colorScheme.onSurfaceVariant
                       )

                       val createdAt = TmdbUtils.formatDate(review.createdAt)
                       val updatedAt = TmdbUtils.formatDate(review.updatedAt)
                       var timestamp = stringResource(id = R.string.created_at_label, createdAt)
                       if (updatedAt != createdAt) {
                           timestamp += "\n${stringResource(id = R.string.updated_at_label, updatedAt)}"
                       }
                       Text(
                           text = timestamp,
                           color = MaterialTheme.colorScheme.onSurfaceVariant,
                           fontSize = 12.sp
                       )
                   }
               }
               if (index != reviews.size - 1) {
                   Divider(
                       color = MaterialTheme.colorScheme.secondary,
                       modifier = Modifier.padding(vertical = 12.dp)
                   )
               }
           }
       } else {
           Text(
               modifier = Modifier
                   .fillMaxWidth()
                   .wrapContentHeight()
                   .padding(horizontal = 24.dp, vertical = 22.dp),
               text = stringResource(R.string.no_reviews_label),
               color = MaterialTheme.colorScheme.tertiary,
               textAlign = TextAlign.Center,
               style = MaterialTheme.typography.headlineMedium
           )
       }
   }
}

@Composable
fun DetailsFor(
    type: MediaViewType,
    itemId: Int,
    mediaItem: DetailedItem?,
    mainViewModel: MainViewModel
) {
    if (type == MediaViewType.MOVIE) {
        MiscMovieDetails(
            itemId = itemId,
            mediaItem = mediaItem,
            mainViewModel = mainViewModel
        )
    } else {
        MiscTvDetails(
            itemId = itemId,
            mediaItem = mediaItem,
            mainViewModel = mainViewModel
        )
    }
}