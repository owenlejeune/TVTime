package com.owenlejeune.tvtime.ui.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.api.common.LoadingState
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.DetailedItem
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.DetailedMovie
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.DetailedTv
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.Genre
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.ImageCollection
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.Review
import com.owenlejeune.tvtime.extensions.DateFormat
import com.owenlejeune.tvtime.extensions.WindowSizeClass
import com.owenlejeune.tvtime.extensions.combineWith
import com.owenlejeune.tvtime.extensions.combinedOnVisibilityChange
import com.owenlejeune.tvtime.extensions.format
import com.owenlejeune.tvtime.extensions.getCalendarYear
import com.owenlejeune.tvtime.extensions.isIn
import com.owenlejeune.tvtime.extensions.lazyPagingItems
import com.owenlejeune.tvtime.extensions.shimmerBackground
import com.owenlejeune.tvtime.preferences.AppPreferences
import com.owenlejeune.tvtime.ui.components.ActionsView
import com.owenlejeune.tvtime.ui.components.AdditionalDetailItem
import com.owenlejeune.tvtime.ui.components.AvatarImage
import com.owenlejeune.tvtime.ui.components.BackButton
import com.owenlejeune.tvtime.ui.components.CastCard
import com.owenlejeune.tvtime.ui.components.ChipDefaults
import com.owenlejeune.tvtime.ui.components.ChipGroup
import com.owenlejeune.tvtime.ui.components.ChipInfo
import com.owenlejeune.tvtime.ui.components.ChipStyle
import com.owenlejeune.tvtime.ui.components.CircleBackgroundColorImage
import com.owenlejeune.tvtime.ui.components.ContentCard
import com.owenlejeune.tvtime.ui.components.DetailHeader
import com.owenlejeune.tvtime.ui.components.ExpandableContentCard
import com.owenlejeune.tvtime.ui.components.ExternalIdsArea
import com.owenlejeune.tvtime.ui.components.HtmlText
import com.owenlejeune.tvtime.ui.components.ImageGalleryOverlay
import com.owenlejeune.tvtime.ui.components.ListContentCard
import com.owenlejeune.tvtime.ui.components.PlaceholderDetailHeader
import com.owenlejeune.tvtime.ui.components.PosterItem
import com.owenlejeune.tvtime.ui.components.RoundedChip
import com.owenlejeune.tvtime.ui.components.RoundedTextField
import com.owenlejeune.tvtime.ui.components.TVTTopAppBar
import com.owenlejeune.tvtime.ui.components.TwoLineImageTextCard
import com.owenlejeune.tvtime.ui.components.VideosCard
import com.owenlejeune.tvtime.ui.components.WatchProvidersCard
import com.owenlejeune.tvtime.ui.navigation.AppNavItem
import com.owenlejeune.tvtime.ui.theme.Typography
import com.owenlejeune.tvtime.ui.viewmodel.ApplicationViewModel
import com.owenlejeune.tvtime.ui.viewmodel.MainViewModel
import com.owenlejeune.tvtime.ui.viewmodel.SpecialFeaturesViewModel
import com.owenlejeune.tvtime.ui.views.extras.SpecialFeaturesViews
import com.owenlejeune.tvtime.utils.SessionManager
import com.owenlejeune.tvtime.utils.TmdbUtils
import com.owenlejeune.tvtime.utils.types.MediaViewType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent

private fun fetchData(
    mainViewModel: MainViewModel,
    itemId: Int,
    type: MediaViewType,
    force: Boolean = false
) {
    val scope = CoroutineScope(Dispatchers.IO)

    scope.launch { mainViewModel.getById(itemId, type, force) }
    scope.launch { mainViewModel.getImages(itemId, type, force) }
    scope.launch { mainViewModel.getExternalIds(itemId, type, force) }
    scope.launch { mainViewModel.getKeywords(itemId, type, force) }
    scope.launch { mainViewModel.getCastAndCrew(itemId, type, force) }
    scope.launch { mainViewModel.getSimilar(itemId, type) }
    scope.launch { mainViewModel.getVideos(itemId, type, force) }
    scope.launch { mainViewModel.getWatchProviders(itemId, type, force) }
    scope.launch { mainViewModel.getReviews(itemId, type, force) }
    if (SessionManager.isLoggedIn) {
        scope.launch { mainViewModel.getAccountStates(itemId, type) }
    }

    when (type) {
        MediaViewType.MOVIE -> {
            scope.launch { mainViewModel.getReleaseDates(itemId, force) }
        }
        MediaViewType.TV -> {
            scope.launch { mainViewModel.getContentRatings(itemId, force) }
        }
        else -> {}
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class, ExperimentalMaterialApi::class)
@Composable
fun MediaDetailScreen(
    appNavController: NavController,
    itemId: Int,
    type: MediaViewType,
    windowSize: WindowSizeClass
) {
    val mainViewModel = viewModel<MainViewModel>()
    val applicationViewModel = viewModel<ApplicationViewModel>()

    applicationViewModel.statusBarColor.value = MaterialTheme.colorScheme.background
    applicationViewModel.navigationBarColor.value = MaterialTheme.colorScheme.background

    val mediaItems: Map<Int, DetailedItem> = remember { mainViewModel.produceDetailsFor(type) }
    val mediaItem = mediaItems[itemId]

    val imagesMap = remember { mainViewModel.produceImagesFor(type) }
    val images = imagesMap[itemId]

    val topAppBarScrollState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarScrollState)

    val pagerState = rememberPagerState(initialPage = 0)

    LaunchedEffect(Unit) {
        fetchData(mainViewModel, itemId, type)
    }

    if (type == MediaViewType.TV) {
        LaunchedEffect(mediaItem) {
            val lastSeason = (mediaItem as DetailedTv?)?.numberOfSeasons ?: 0
            if (lastSeason > 0) {
                mainViewModel.getSeason(itemId, lastSeason)
            }
        }
    }

    val isRefreshing = remember { mutableStateOf(false) }
    mainViewModel.monitorDetailsLoadingRefreshing(refreshing = isRefreshing)

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing.value,
        onRefresh = {
            fetchData(mainViewModel, itemId, type, true)
        }
    )

    val titleViewHidden = remember { mutableStateOf(false) }
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        val showGalleryOverlay = remember { mutableStateOf(false) }
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TVTTopAppBar(
                    scrollBehavior = scrollBehavior,
                    title = {
                        AnimatedVisibility(
                            visible = titleViewHidden.value,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Text(text = mediaItem?.title ?: "")
                        }
                    },
                    appNavController = appNavController,
                    navigationIcon = {
                        BackButton(navController = appNavController)
                    }
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier
                .padding(innerPadding)
                .pullRefresh(state = pullRefreshState)
            ) {
                MediaViewContent(
                    appNavController = appNavController,
                    itemId = itemId,
                    mediaItem = mediaItem,
                    images = images,
                    type = type,
                    windowSize = windowSize,
                    showImageGallery = showGalleryOverlay,
                    pagerState = pagerState,
                    mainViewModel = mainViewModel,
                    titleViewHidden = titleViewHidden
                )
                PullRefreshIndicator(
                    refreshing = isRefreshing.value,
                    state = pullRefreshState,
                    modifier = Modifier.align(alignment = Alignment.TopCenter)
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
fun MediaViewContent(
    appNavController: NavController,
    mainViewModel: MainViewModel,
    itemId: Int,
    mediaItem: DetailedItem?,
    images: ImageCollection?,
    type: MediaViewType,
    windowSize: WindowSizeClass,
    showImageGallery: MutableState<Boolean>,
    pagerState: PagerState,
    titleViewHidden: MutableState<Boolean>,
    preferences: AppPreferences = KoinJavaComponent.get(AppPreferences::class.java)
) {
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
            val detailsLoadingState = remember { mainViewModel.produceDetailsLoadingStateFor(type) }
            val isLoading = detailsLoadingState.value.isIn(LoadingState.LOADING, LoadingState.REFRESHING)

            if (isLoading) {
                PlaceholderDetailHeader()
            } else {
                DetailHeader(
                    posterUrl = TmdbUtils.getFullPosterPath(mediaItem?.posterPath),
                    posterContentDescription = mediaItem?.title,
                    backdropUrl = TmdbUtils.getFullBackdropPath(mediaItem?.backdropPath),
                    rating = mediaItem?.voteAverage?.let { it / 10 },
                    imageCollection = images,
                    showGalleryOverlay = showImageGallery,
                    pagerState = pagerState
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DetailsFor(
                    type = type,
                    mediaItem = mediaItem,
                    mainViewModel = mainViewModel,
                    itemId =  itemId,
                    titleViewHidden = titleViewHidden
                )

                ExternalIdsArea(
                    modifier = Modifier.padding(start = 20.dp),
                    type = type,
                    itemId = itemId
                )

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

                    CastArea(
                        itemId = itemId,
                        appNavController = appNavController,
                        type = type,
                        mainViewModel = mainViewModel
                    )

                    if (type == MediaViewType.TV) {
                        SeasonCard(
                            itemId = itemId,
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

                    VideosArea(
                        itemId = itemId,
                        modifier = Modifier.fillMaxWidth(),
                        mainViewModel = mainViewModel,
                        type = type
                    )

                    AdditionalDetailsCard(mediaItem = mediaItem, type = type)

                    WatchProvidersArea(itemId = itemId, type = type, mainViewModel = mainViewModel)

                    if (
                        mediaItem?.productionCompanies?.firstOrNull { it.name == "Marvel Studios" } != null
                        && preferences.showNextMcuProduction
                    ) {
                        NextMcuProjectCard(itemId = itemId, appNavController = appNavController)
                    }

                    SpecialFeaturesViews.viewsMap[itemId]?.invoke()

                    if (windowSize != WindowSizeClass.Expanded) {
                        ReviewsCard(itemId = itemId, type = type, mainViewModel = mainViewModel, windowSize = windowSize)
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
                ReviewsCard(itemId = itemId, type = type, mainViewModel = mainViewModel, windowSize = windowSize)

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun MiscTvDetails(
    itemId: Int,
    mediaItem: DetailedItem?,
    titleViewHidden: MutableState<Boolean>,
    mainViewModel: MainViewModel
) {
    mediaItem?.let { tv ->
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
            contentRating = contentRating,
            type = MediaViewType.TV,
            title = mediaItem.title,
            titleViewHidden = titleViewHidden
        )
    }
}

@Composable
private fun MiscMovieDetails(
    itemId: Int,
    mediaItem: DetailedItem?,
    titleViewHidden: MutableState<Boolean>,
    mainViewModel: MainViewModel
) {
    val movie = mediaItem as? DetailedMovie

    val contentRatingsMap = remember { mainViewModel.movieReleaseDates }
    val contentRating = TmdbUtils.getMovieRating(contentRatingsMap[itemId])

    MiscDetails(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 16.dp),
        year = movie?.releaseDate?.getCalendarYear()?.toString() ?: "",
        runtime = TmdbUtils.convertRuntimeToHoursMinutes(movie),
        genres = movie?.genres ?: emptyList(),
        contentRating = contentRating,
        type = MediaViewType.MOVIE,
        title = mediaItem?.title ?: "",
        titleViewHidden = titleViewHidden
    )
}

@Composable
private fun MiscDetails(
    modifier: Modifier = Modifier,
    title: String,
    year: String,
    runtime: String,
    genres: List<Genre>,
    contentRating: String,
    type: MediaViewType,
    titleViewHidden: MutableState<Boolean>,
) {
    val mainViewModel = viewModel<MainViewModel>()
    val detailsLoadingState = remember { mainViewModel.produceDetailsLoadingStateFor(type) }
    val isLoading = detailsLoadingState.value.isIn(LoadingState.LOADING, LoadingState.REFRESHING)
    val shimmerModifier = if (isLoading) {
        Modifier
            .shimmerBackground(RoundedCornerShape(5.dp))
            .width(40.dp)
    } else {
        Modifier
    }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title.takeUnless { isLoading } ?: "",
            color = MaterialTheme.colorScheme.secondary,
            style = Typography.headlineLarge,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = shimmerModifier
                .width(400.dp)
                .combinedOnVisibilityChange(
                    onVisible = { titleViewHidden.value = false },
                    onNotVisible = { titleViewHidden.value = true }
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.spacedBy(if (isLoading) 6.dp else 0.dp)
        ) {
            Text(
                text = year.takeUnless { isLoading } ?: "",
                color = MaterialTheme.colorScheme.onBackground,
                modifier = shimmerModifier
            )
            if (runtime != "0m" || isLoading) {
                Text(
                    text = runtime.takeUnless { isLoading } ?: "",
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = shimmerModifier.padding(start = 12.dp)
                )
            }
            Text(
                text = contentRating.takeUnless { isLoading } ?: "",
                color = MaterialTheme.colorScheme.onBackground,
                modifier = shimmerModifier.padding(start = 12.dp)
            )
        }

        val chips = if (isLoading) Genre.placeholderData else genres
        ChipGroup(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            chips = chips.map { ChipInfo(it.name, false) },
            isLoading = isLoading
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
    val keywordsMap = remember { mainViewModel.produceKeywordsFor(type) }
    val keywords = keywordsMap[itemId]

    val detailsLoadingState = remember { mainViewModel.produceAccountStatesLoadingStateFor(type) }
    val isLoading = detailsLoadingState.value.isIn(LoadingState.LOADING, LoadingState.REFRESHING)

    if (isLoading) {
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
                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .width(150.dp)
                        .shimmerBackground(RoundedCornerShape(10.dp)),
                    text = "",
                    color = MaterialTheme.colorScheme.tertiary,
                    style = MaterialTheme.typography.bodyLarge,
                    fontStyle = FontStyle.Italic,
                )
                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .height(100.dp)
                        .shimmerBackground(RoundedCornerShape(10.dp)),
                    text = "",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )


                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(ChipStyle.Rounded.mainAxisSpacing)
                ) {
                    Spacer(modifier = Modifier.width(8.dp))
                    ChipInfo.generatePlaceholders(4).forEach { keywordChipInfo ->
                        RoundedChip(
                            modifier = Modifier.width(60.dp),
                            isLoading = true,
                            chipInfo = keywordChipInfo,
                            colors = ChipDefaults.roundedChipColors(
                                unselectedContainerColor = MaterialTheme.colorScheme.primary,
                                unselectedContentColor = MaterialTheme.colorScheme.primary
                            ),
                            onSelectionChanged = {}
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    } else {
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
                                    color = MaterialTheme.colorScheme.tertiary,
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
                                            appNavController.navigate(
                                                AppNavItem.KeywordsView.withArgs(
                                                    type,
                                                    chip.text,
                                                    chip.id!!
                                                )
                                            )
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
private fun CastArea(
    itemId: Int,
    type: MediaViewType,
    mainViewModel: MainViewModel,
    appNavController: NavController,
    modifier: Modifier = Modifier
) {
    val castMap = remember { mainViewModel.produceCastFor(type) }
    val cast = castMap[itemId]

    val loadingState = remember { mainViewModel.produceDetailsLoadingStateFor(type) }
    val isLoading = loadingState.value.isIn(LoadingState.LOADING, LoadingState.REFRESHING)

    CastCard(
        modifier = modifier,
        title = stringResource(R.string.cast_label),
        isLoading = isLoading,
        cast = cast,
        appNavController = appNavController,
        onSeeMore = {
            appNavController.navigate(
                AppNavItem.CastCrewListView.withArgs(
                    type,
                    itemId
                )
            )
        }
    )
}

@Composable
private fun SeasonCard(
    itemId: Int,
    mainViewModel: MainViewModel,
    appNavController: NavController
) {
    val seasonsMap = remember { mainViewModel.tvSeasons }
    val lastSeason = seasonsMap[itemId]?.maxByOrNull { it.seasonNumber }

    lastSeason?.let {
        ContentCard(
            title = stringResource(R.string.latest_season_title)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(all = 12.dp)
                    .clickable {
                        appNavController.navigate(
                            AppNavItem.DetailView.withArgs(
                                MediaViewType.SEASON,
                                itemId.combineWith(it.seasonNumber)
                            )
                        )
                    }
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
    val similarContentMap = remember { mainViewModel.produceSimilarContentFor(mediaType) }
    val similarContent = similarContentMap[itemId]
    val pagingItems = similarContent?.collectAsLazyPagingItems()

    pagingItems?.let {
        if (it.itemCount > 0) {
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
}

@Composable
fun VideosArea(
    itemId: Int,
    type: MediaViewType,
    mainViewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val videosMap = remember { mainViewModel.produceVideosFor(type) }
    val videos = videosMap[itemId]

    if (videos?.any { it.isOfficial } == true) {
        VideosCard(videos = videos, modifier = modifier)
    }
}

@SuppressLint("AutoboxingStateValueProperty")
@Composable
private fun WatchProvidersArea(
    itemId: Int,
    type: MediaViewType,
    mainViewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val watchProvidersMap = remember { mainViewModel.produceWatchProvidersFor(type) }
    val watchProviders = watchProvidersMap[itemId]
    watchProviders?.let { providers ->
        if (providers.buy?.isNotEmpty() == true || providers.rent?.isNotEmpty() == true || providers.flaterate?.isNotEmpty() == true) {
            WatchProvidersCard(providers = providers, modifier = modifier)
        }
    }
}

@Composable
private fun NextMcuProjectCard(
    itemId: Int,
    appNavController: NavController,
    modifier: Modifier = Modifier
) {
    val viewModel = viewModel<SpecialFeaturesViewModel>()

    LaunchedEffect(Unit) {
        viewModel.getNextMcuProject()
    }

    val nextMcuProject = remember { viewModel.nextMcuProject }

    if (nextMcuProject.value?.id != itemId) {
        Card(
            shape = RoundedCornerShape(10.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = modifier.then(
                Modifier
                    .fillMaxWidth()
                    .clickable {
                        nextMcuProject.value?.let {
                            appNavController.navigate(
                                AppNavItem.DetailView.withArgs(
                                    it.type,
                                    it.id
                                )
                            )
                        }
                    }
            )
        ) {
            Box(
                modifier = Modifier.height(250.dp)
            ) {
                val model = ImageRequest.Builder(LocalContext.current)
                    .data(nextMcuProject.value?.posterUrl)
                    .diskCacheKey(nextMcuProject.value?.title)
                    .networkCachePolicy(CachePolicy.ENABLED)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .build()

                Column(
                    modifier = Modifier.padding(all = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = stringResource(R.string.next_in_the_mcu_title),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.W700,
                        modifier = Modifier.padding(start = 2.dp)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AsyncImage(
                            model = model,
                            modifier = Modifier
                                .aspectRatio(0.7f)
                                .clip(RoundedCornerShape(10.dp)),
                            contentDescription = nextMcuProject.value?.title
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = nextMcuProject.value?.title ?: "",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.W600
                            )

                            val releaseDate =
                                nextMcuProject.value?.releaseDate?.format(DateFormat.MMMM_dd) ?: ""
                            val daysLeft = stringResource(
                                id = R.string.days_left,
                                nextMcuProject.value?.daysUntil ?: -1
                            )
                            Text(
                                text = "$releaseDate • $daysLeft",
                                fontStyle = FontStyle.Italic
                            )

                            Text(
                                text = nextMcuProject.value?.overview ?: "",
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReviewsCard(
    itemId: Int,
    type: MediaViewType,
    windowSize: WindowSizeClass,
    mainViewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val reviewsMap = remember { mainViewModel.produceReviewsFor(type) }
    val reviews = reviewsMap[itemId]

    if (windowSize == WindowSizeClass.Expanded) {
        ListContentCard(
            modifier = modifier,
            header = {
                ReviewsCardHeader()
            },
        ) {
            ReviewsCardContent(reviews = reviews)
        }
    } else {
        ExpandableContentCard(
            modifier = modifier,
            title = {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp)
                ) {
                    ReviewsCardHeader()
                }
            }
        ) { isExpanded ->
            val reviewsSubList = reviews?.let { 
                if (isExpanded) {
                    reviews
                } else {
                    reviews.subList(0, 3)
                }
            } ?: emptyList()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ReviewsCardContent(reviews = reviewsSubList)
            }
        }
    }
}

@Composable
private fun ReviewsCardHeader() {
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
}

@Composable
private fun ReviewsCardContent(
    reviews: List<Review>?
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

@Composable
fun DetailsFor(
    type: MediaViewType,
    itemId: Int,
    mediaItem: DetailedItem?,
    titleViewHidden: MutableState<Boolean>,
    mainViewModel: MainViewModel
) {
    if (type == MediaViewType.MOVIE) {
        MiscMovieDetails(
            itemId = itemId,
            mediaItem = mediaItem,
            mainViewModel = mainViewModel,
            titleViewHidden = titleViewHidden
        )
    } else {
        MiscTvDetails(
            itemId = itemId,
            mediaItem = mediaItem,
            mainViewModel = mainViewModel,
            titleViewHidden = titleViewHidden
        )
    }
}

