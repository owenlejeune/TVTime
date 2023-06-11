package com.owenlejeune.tvtime.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.api.tmdb.api.v3.AccountService
import com.owenlejeune.tvtime.api.tmdb.api.v3.DetailService
import com.owenlejeune.tvtime.api.tmdb.api.v3.MoviesService
import com.owenlejeune.tvtime.api.tmdb.api.v3.TvService
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.*
import com.owenlejeune.tvtime.extensions.WindowSizeClass
import com.owenlejeune.tvtime.extensions.listItems
import com.owenlejeune.tvtime.preferences.AppPreferences
import com.owenlejeune.tvtime.ui.components.*
import com.owenlejeune.tvtime.ui.navigation.AppNavItem
import com.owenlejeune.tvtime.utils.types.TabNavItem
import com.owenlejeune.tvtime.ui.components.DetailHeader
import com.owenlejeune.tvtime.utils.types.MediaViewType
import com.owenlejeune.tvtime.ui.components.Tabs
import com.owenlejeune.tvtime.ui.theme.FavoriteSelected
import com.owenlejeune.tvtime.ui.theme.RatingSelected
import com.owenlejeune.tvtime.ui.theme.WatchlistSelected
import com.owenlejeune.tvtime.ui.theme.actionButtonColor
import com.owenlejeune.tvtime.utils.SessionManager
import com.owenlejeune.tvtime.utils.TmdbUtils
import kotlinx.coroutines.*
import org.json.JSONObject
import org.koin.java.KoinJavaComponent.get
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun MediaDetailScreen(
    appNavController: NavController,
    itemId: Int?,
    type: MediaViewType,
    windowSize: WindowSizeClass,
    preferences: AppPreferences = get(AppPreferences::class.java)
) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(color = MaterialTheme.colorScheme.background)
    systemUiController.setNavigationBarColor(color = MaterialTheme.colorScheme.background)

    val service = when (type) {
        MediaViewType.MOVIE -> MoviesService()
        MediaViewType.TV -> TvService()
        else -> throw IllegalArgumentException("Media type given: ${type}, \n     expected one of MediaViewType.MOVIE, MediaViewType.TV") // shouldn't happen
    }

    val mediaItem = remember { mutableStateOf<DetailedItem?>(null) }
    itemId?.let {
        if (mediaItem.value == null) {
            fetchMediaItem(itemId, service, mediaItem)
        }
    }

    val images = remember { mutableStateOf<ImageCollection?>(null) }
    itemId?.let {
        if (preferences.showBackdropGallery && images.value == null) {
            fetchImages(itemId, service, images)
        }
    }

    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val topAppBarScrollState = rememberTopAppBarScrollState()
    val scrollBehavior = remember(decayAnimationSpec) {
        TopAppBarDefaults.pinnedScrollBehavior(topAppBarScrollState)
    }

    val pagerState = rememberPagerState(initialPage = 0)

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        val showGalleryOverlay = remember { mutableStateOf(false) }
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                SmallTopAppBar(
                    scrollBehavior = scrollBehavior,
                    colors = TopAppBarDefaults
                        .smallTopAppBarColors(
                            scrolledContainerColor = MaterialTheme.colorScheme.background,
                            titleContentColor = MaterialTheme.colorScheme.primary
                        ),
                    title = { Text(text = mediaItem.value?.title ?: "") },
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
                    service = service,
                    type = type,
                    windowSize = windowSize,
                    showImageGallery = showGalleryOverlay,
                    pagerState = pagerState
                )
            }
        }

        if (showGalleryOverlay.value) {
            images.value?.let {
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
    itemId: Int?,
    mediaItem: MutableState<DetailedItem?>,
    images: MutableState<ImageCollection?>,
    service: DetailService,
    type: MediaViewType,
    windowSize: WindowSizeClass,
    showImageGallery: MutableState<Boolean>,
    pagerState: PagerState
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
            DetailHeader(
                posterUrl = TmdbUtils.getFullPosterPath(mediaItem.value?.posterPath),
                posterContentDescription = mediaItem.value?.title,
                backdropUrl = TmdbUtils.getFullBackdropPath(mediaItem.value?.backdropPath),
                rating = mediaItem.value?.voteAverage?.let { it / 10 },
                imageCollection = images.value,
                showGalleryOverlay = showImageGallery,
                pagerState = pagerState
            )

            Column(
//                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (type == MediaViewType.MOVIE) {
                    MiscMovieDetails(mediaItem = mediaItem, service as MoviesService)
                } else {
                    MiscTvDetails(mediaItem = mediaItem, service as TvService)
                }

                ActionsView(itemId = itemId, type = type, service = service)

                if (type == MediaViewType.MOVIE) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        MainContent(
                            itemId = itemId,
                            mediaItem = mediaItem,
                            type = type,
                            service = service,
                            appNavController = appNavController,
                            windowSize = windowSize
                        )
                    }
                } else {
                    val tabState = rememberPagerState()
                    val tabs  = listOf(DetailsTab, SeasonsTab)
                    TvSeriesTabs(pagerState = tabState, tabs = tabs)
                    HorizontalPager(
                        count = tabs.size,
                        state = tabState,
                        userScrollEnabled = false
                    ) { page ->
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            when (tabs[page]) {
                                is DetailsTab -> {
                                    MainContent(
                                        itemId = itemId,
                                        mediaItem = mediaItem,
                                        type = type,
                                        service = service,
                                        appNavController = appNavController,
                                        windowSize = windowSize
                                    )
                                }

                                is SeasonsTab -> {
                                    SeasonsTab(
                                        itemId = itemId,
                                        mediaItem = mediaItem,
                                        service = service as TvService
                                    )
                                }
                            }
                        }
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
                ReviewsCard(itemId = itemId, service = service)

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun TvSeriesTabs(
    pagerState: PagerState,
    tabs: List<TabNavItem>
) {
    Tabs(
        modifier = Modifier.offset(y = (-16).dp),
        pagerState = pagerState,
        tabs = tabs
    )
}

@Composable
private fun SeasonsTab(
    itemId: Int?,
    mediaItem: MutableState<DetailedItem?>,
    service: TvService
) {
    val scope = rememberCoroutineScope()

    mediaItem.value?.let { tv ->
        val series = tv as DetailedTv

        val seasons = remember { mutableStateMapOf<Int, Season>() }
        LaunchedEffect(Unit) {
            itemId?.let {
                for (i in 0..series.numberOfSeasons) {
                    scope.launch {
                        val season = service.getSeason(it, i)
                        if (season.isSuccessful) {
                            seasons[i] = season.body()!!
                        }
                    }
                }
            }
        }

        seasons.toSortedMap().values.forEach { season ->
            SeasonSection(season = season)
        }
    }
}

@Composable
private fun SeasonSection(season: Season) {
    var isExpanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color = MaterialTheme.colorScheme.surfaceVariant)
            .clickable {
                isExpanded = !isExpanded
            }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = season.name,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.weight(1f))

            var currentRotation by remember { mutableStateOf(0f) }
            val rotation = remember { androidx.compose.animation.core.Animatable(currentRotation) }
            LaunchedEffect(isExpanded) {
                rotation.animateTo(
                    targetValue = if (isExpanded) 180f else 0f,
                    animationSpec = tween(200, easing = LinearEasing)
                ) {
                    currentRotation = value
                }
            }
            Icon(
                imageVector = Icons.Outlined.ExpandMore,
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
                    .rotate(currentRotation),
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
    AnimatedVisibility(
        visible = isExpanded,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            season.episodes.forEachIndexed { index, episode ->
                EpisodeItem(episode = episode)
                if (index != season.episodes.size - 1) {
                    Divider()
                }
            }
        }
    }
}

@Composable
private fun EpisodeItem(episode: Episode) {
    val height = 170.dp
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.height(height)
    ) {
        AsyncImage(
            model = TmdbUtils.getFullEpisodeStillPath(episode),
            contentDescription = null,
            modifier = Modifier
                .size(width = 100.dp, height = height)
                .clip(RoundedCornerShape(10.dp)),
            contentScale = ContentScale.Crop
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "S${episode.seasonNumber}E${episode.episodeNumber} • ${episode.name}",
                color = MaterialTheme.colorScheme.secondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            TmdbUtils.convertEpisodeDate(episode.airDate)?.let {
                Text(
                    text = it,
                    fontStyle = FontStyle.Italic,
                    fontSize = 12.sp
                )
            }
            Text(
                text = episode.overview,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
private fun MainContent(
    itemId: Int?,
    mediaItem: MutableState<DetailedItem?>,
    type: MediaViewType,
    service: DetailService,
    appNavController: NavController,
    windowSize: WindowSizeClass
) {
    OverviewCard(itemId = itemId, mediaItem = mediaItem, service = service)

    CastCard(itemId = itemId, service = service, appNavController = appNavController)

    SimilarContentCard(itemId = itemId, service = service, mediaType = type, appNavController = appNavController)

    VideosCard(itemId = itemId, service = service, modifier = Modifier.fillMaxWidth())

    AdditionalDetailsCard(itemId = itemId, mediaItem = mediaItem, service = service, type = type)

    if (windowSize != WindowSizeClass.Expanded) {
        ReviewsCard(itemId = itemId, service = service)
    }
}

@Composable
private fun MiscTvDetails(mediaItem: MutableState<DetailedItem?>, service: TvService) {
    mediaItem.value?.let { tv ->
        val series = tv as DetailedTv

        val contentRating = remember { mutableStateOf("") }
        fetchTvContentRating(series.id, service, contentRating)

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
private fun MiscMovieDetails(mediaItem: MutableState<DetailedItem?>, service: MoviesService) {
    mediaItem.value?.let { mi ->
        val movie = mi as DetailedMovie

        val contentRating = remember { mutableStateOf("") }
        fetchMovieContentRating(movie.id, service, contentRating)

        MiscDetails(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 16.dp),
            year = TmdbUtils.getMovieReleaseYear(movie),
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
    contentRating: MutableState<String>
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
                text = contentRating.value,
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
private fun ActionsView(
    itemId: Int?,
    type: MediaViewType,
    service: DetailService,
    modifier: Modifier = Modifier
) {
    itemId?.let {
        val session = SessionManager.currentSession.value
        Row(
            modifier = modifier
                .wrapContentSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RateButton(
                itemId = itemId,
                type = type,
                service = service
            )

            if (session?.isAuthorized == true) {
                val accountService = AccountService()
                WatchlistButton(
                    itemId = itemId,
                    type = type
                )
                ListButton(
                    itemId = itemId,
                    type = type,
                    service = accountService
                )
                FavoriteButton(
                    itemId = itemId,
                    type = type
                )
            }
        }
    }
}

@Composable
fun ActionButton(
    modifier: Modifier = Modifier,
    itemId: Int,
    type: MediaViewType,
    iconRes: Int,
    contentDescription: String,
    isSelected: Boolean,
    filledIconColor: Color,
    onClick: (Context, Int, MediaViewType, MutableState<Boolean>, (Boolean) -> Unit) -> Unit = { _, _, _, _, _ -> }//(MutableState<Boolean>) -> Unit = { _ -> }
) {
    val context = LocalContext.current
    val session = SessionManager.currentSession

    val hasSelected = remember { mutableStateOf(isSelected) }

    val bgColor = MaterialTheme.colorScheme.background
    val tintColor = remember { Animatable(if (hasSelected.value) filledIconColor else bgColor) }

    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .clip(CircleShape)
            .height(40.dp)
            .requiredWidthIn(min = 40.dp)
            .background(color = MaterialTheme.colorScheme.actionButtonColor)
            .clickable(
                onClick = {
                    if (session != null) {
                        onClick(context, itemId, type, hasSelected) {
                            coroutineScope.launch {
                                tintColor.animateTo(
                                    targetValue = if (it) filledIconColor else bgColor,
                                    animationSpec = tween(200)
                                )
                            }
                        }
                    }
                }
            )
    ) {
        Icon(
            modifier = Modifier
                .clip(CircleShape)
                .align(Alignment.Center),
            painter = painterResource(id = iconRes),
            contentDescription = contentDescription,
            tint = tintColor.value
        )
    }
}

@Composable
private fun RateButton(
    itemId: Int,
    type: MediaViewType,
    service: DetailService,
    modifier: Modifier = Modifier
) {
    val session = SessionManager.currentSession.value
    val context = LocalContext.current

    val itemIsRated = remember {
        mutableStateOf(
            if (type == MediaViewType.MOVIE) {
                session?.hasRatedMovie(itemId) == true
            } else {
                session?.hasRatedTvShow(itemId) == true
            }
        )
    }

    val showSessionDialog = remember { mutableStateOf(false) }
    val showRatingDialog = remember { mutableStateOf(false) }

    val bgColor = MaterialTheme.colorScheme.background
    val filledColor = RatingSelected
    val tintColor = remember { Animatable(if (itemIsRated.value) filledColor else bgColor) }

    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .animateContentSize(tween(durationMillis = 100))
            .clip(CircleShape)
            .height(40.dp)
            .requiredWidthIn(min = 40.dp)
            .background(color = MaterialTheme.colorScheme.actionButtonColor)
            .clickable(
                onClick = {
                    if (session == null) {
                        showSessionDialog.value = true
                    } else {
                        showRatingDialog.value = true
                    }
                }
            ),
    ) {
        Icon(
            modifier = Modifier
                .clip(CircleShape)
                .align(Alignment.Center),
            painter = painterResource(id = R.drawable.ic_rating_star),
            contentDescription = "",
            tint = tintColor.value
        )
    }

    CreateSessionDialog(showDialog = showSessionDialog, onSessionReturned = {})

    val userRating = session?.getRatingForId(itemId, type) ?: 0f
    RatingDialog(showDialog = showRatingDialog, rating = userRating, onValueConfirmed = { rating ->
        if (rating > 0f) {
            postRating(context, rating, itemId, service, itemIsRated)
            coroutineScope.launch {
                tintColor.animateTo(targetValue = filledColor, animationSpec = tween(300))
            }
        } else {
            deleteRating(context, itemId, service, itemIsRated)
            coroutineScope.launch {
                tintColor.animateTo(targetValue = bgColor, animationSpec = tween(300))
            }
        }
    })
}

@Composable
fun WatchlistButton(
    itemId: Int,
    type: MediaViewType,
    modifier: Modifier = Modifier
) {
    val session = SessionManager.currentSession.value

    val hasWatchlistedItem = if (type == MediaViewType.MOVIE) {
        session?.hasWatchlistedMovie(itemId) == true
    } else {
        session?.hasWatchlistedTvShow(itemId) == true
    }

    ActionButton(
        modifier = modifier,
        itemId = itemId,
        type = type,
        iconRes = R.drawable.ic_watchlist,
        contentDescription = "",
        isSelected = hasWatchlistedItem,
        filledIconColor = WatchlistSelected,
        onClick = ::addToWatchlist
    )
}

@Composable
fun ListButton(
    itemId: Int,
    type: MediaViewType,
    modifier: Modifier = Modifier,
    service: AccountService
) {
    val session = SessionManager.currentSession

//    val hasListedItem

    val showSessionDialog = remember { mutableStateOf(false) }

    CircleBackgroundColorImage(
        modifier = modifier.clickable(
            onClick = {
                if (session == null) {
                    showSessionDialog.value = true
                } else {
                    // add to watchlsit
                }
            }
        ),
        size = 40.dp,
        backgroundColor = MaterialTheme.colorScheme.actionButtonColor,
        painter = painterResource(id = R.drawable.ic_add_to_list),
        colorFilter = ColorFilter.tint(color = /*if (hasWatchlistedItem.value) WatchlistSelected else*/ MaterialTheme.colorScheme.background),
        contentDescription = ""
    )

    CreateSessionDialog(showDialog = showSessionDialog, onSessionReturned = {})
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FavoriteButton(
    itemId: Int,
    type: MediaViewType,
    modifier: Modifier = Modifier
) {
    val session = SessionManager.currentSession.value
    val isFavourited = if (type == MediaViewType.MOVIE) {
        session?.hasFavoritedMovie(itemId) == true
    } else {
        session?.hasFavoritedTvShow(itemId) == true
    }

    ActionButton(
        modifier = modifier,
        itemId = itemId,
        type = type,
        iconRes = R.drawable.ic_favorite,
        contentDescription = "",
        isSelected = isFavourited,
        filledIconColor = FavoriteSelected,
        onClick = ::mediaAddToFavorite
    )
}

@Composable
private fun CreateSessionDialog(showDialog: MutableState<Boolean>, onSessionReturned: (Boolean) -> Unit) {
    if (showDialog.value) {
        AlertDialog(
            modifier = Modifier.wrapContentHeight(),
            onDismissRequest = { showDialog.value = false },
            title = { Text(text = stringResource(R.string.sign_in_dialog_title)) },
            confirmButton = {},
            dismissButton = {
                TextButton(
                    modifier = Modifier.height(40.dp),
                    onClick = {
                        showDialog.value = false
                    }
                ) {
                    Text(text = stringResource(R.string.action_cancel))
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            showDialog.value = false
                        }
                    ) {
                        Text(text = stringResource(R.string.action_sign_in))
                    }
                }
            }
        )
    }
}

@Composable
private fun RatingDialog(showDialog: MutableState<Boolean>, rating: Float, onValueConfirmed: (Float) -> Unit) {

    fun formatPosition(position: Float): String {
        return DecimalFormat("#.#").format(position.toInt()*5/10f)
    }

    if (showDialog.value) {
        var sliderPosition by remember { mutableStateOf(rating) }
        val formatted = formatPosition(sliderPosition).toFloat()
        AlertDialog(
            modifier = Modifier.wrapContentHeight(),
            onDismissRequest = { showDialog.value = false },
            title = { Text(text = stringResource(R.string.rating_dialog_title)) },
            confirmButton = {
                Button(
                    modifier = Modifier.height(40.dp),
                    onClick = {
                        onValueConfirmed.invoke(formatted)
                        showDialog.value = false
                    }
                ) {
                    Text(
                        text = if (formatted > 0f) {
                            stringResource(id = R.string.rating_dialog_confirm)
                        } else {
                            stringResource(id = R.string.rating_dialog_delete)
                        }
                    )
                }
            },
            dismissButton = {
                Button(
                    modifier = Modifier.height(40.dp),
                    onClick = {
                        showDialog.value = false
                    }
                ) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
            text = {
                SliderWithLabel(
                    value = sliderPosition,
                    valueRange = 0f..20f,
                    onValueChanged = {
                        sliderPosition = it
                     },
                    sliderLabel = "${sliderPosition.toInt() * 5}%",
                )
            }
        )
    }
}

@Composable
private fun OverviewCard(
    modifier: Modifier = Modifier,
    itemId: Int?,
    mediaItem: MutableState<DetailedItem?>,
    service: DetailService
) {
    val keywordResponse = remember { mutableStateOf<KeywordsResponse?>(null) }
    if (itemId != null) {
        if (keywordResponse.value == null) {
            fetchKeywords(itemId, service, keywordResponse)
        }
    }

    mediaItem.value?.let { mi ->
        if (!mi.tagline.isNullOrEmpty() || keywordResponse.value?.keywords?.isNotEmpty() == true || !mi.overview.isNullOrEmpty()) {
            ContentCard(
                modifier = modifier
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Spacer(modifier = Modifier.height(12.dp))
                    mi.tagline?.let { tagline ->
                        if (tagline.isNotEmpty()) {
                            Text(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                text = tagline,
                                color = MaterialTheme.colorScheme.primary,
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


                    keywordResponse.value?.keywords?.let { keywords ->
                        val keywordsChipInfo = keywords.map { ChipInfo(it.name, false) }
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(ChipStyle.Rounded.mainAxisSpacing)
                        ) {
                            Spacer(modifier = Modifier.width(8.dp))
                            keywordsChipInfo.forEach { keywordChipInfo ->
                                RoundedChip(
                                    text = keywordChipInfo.text,
                                    enabled = keywordChipInfo.enabled,
                                    colors = ChipDefaults.roundedChipColors(
                                        unselectedContainerColor = MaterialTheme.colorScheme.primary,
                                        unselectedContentColor = MaterialTheme.colorScheme.primary
                                    ),
                                    onSelectionChanged = { chip ->
//                                        if (service is MoviesService) {
//                                            // Toast.makeText(context, chip, Toast.LENGTH_SHORT).show()
//                                        }
                                    }
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun AdditionalDetailsCard(
    modifier: Modifier = Modifier,
    itemId: Int?,
    mediaItem: MutableState<DetailedItem?>,
    service: DetailService,
    type: MediaViewType
) {
    mediaItem.value?.let { mi ->
        ContentCard(
            modifier = modifier,
            title = stringResource(R.string.additional_details_title)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(vertical = 12.dp, horizontal = 16.dp),
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
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = subtext,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (includeDivider) {
            Divider()
        }
    }
}


@Composable
private fun CastCard(itemId: Int?, service: DetailService, appNavController: NavController, modifier: Modifier = Modifier) {
    val castAndCrew = remember { mutableStateOf<CastAndCrew?>(null) }
    itemId?.let {
        if (castAndCrew.value == null) {
            fetchCastAndCrew(itemId, service, castAndCrew)
        }
    }

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
            items(castAndCrew.value?.cast?.size ?: 0) { i ->
                val castMember = castAndCrew.value!!.cast[i]
                CastCrewCard(appNavController = appNavController, person = castMember)
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
            is CastMember -> person.character
            is CrewMember -> person.job
            else -> null
        },
        imageUrl = TmdbUtils.getFullPersonImagePath(person),
        titleTextColor = MaterialTheme.colorScheme.onPrimary,
        subtitleTextColor = MaterialTheme.colorScheme.onSecondary,
        onItemClicked = {
            appNavController.navigate(
                "${AppNavItem.DetailView.route}/${MediaViewType.PERSON}/${person.id}"
            )
        }
    )
}

@Composable
fun SimilarContentCard(
    itemId: Int?,
    service: DetailService,
    mediaType: MediaViewType,
    appNavController: NavController,
    modifier: Modifier = Modifier
) {
    val similarContent = remember { mutableStateOf<HomePageResponse?>(null) }
    itemId?.let {
        if (similarContent.value == null) {
            fetchSimilarContent(itemId, service, similarContent)
        }
    }

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
            items(similarContent.value?.results?.size ?: 0) { i ->
                val content = similarContent.value!!.results[i]

                TwoLineImageTextCard(
                    title = content.title,
                    modifier = Modifier
                        .width(124.dp)
                        .wrapContentHeight(),
                    imageUrl = TmdbUtils.getFullPosterPath(content),
                    onItemClicked = {
                        appNavController.navigate(
                            "${AppNavItem.DetailView.route}/${mediaType}/${content.id}"
                        )
                    },
                    placeholder = Icons.Filled.Movie
                )
            }
            item {
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@Composable
fun VideosCard(
    itemId: Int?,
    service: DetailService,
    modifier: Modifier = Modifier
) {
    val videoResponse = remember { mutableStateOf<VideoResponse?>(null) }
    itemId?.let {
        if (videoResponse.value == null) {
            fetchVideos(itemId, service, videoResponse)
        }
    }

    if (videoResponse.value != null && videoResponse.value!!.results.any { it.isOfficial }) {
        val results = videoResponse.value!!.results
        ExpandableContentCard(
            modifier = modifier,
            title = {
                Text(
                    text = stringResource(id = R.string.videos_label),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 8.dp, top = 8.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            toggleTextColor = MaterialTheme.colorScheme.primary
        ) { isExpanded ->
            VideoGroup(
                results = results,
                type = Video.Type.TRAILER,
                title = stringResource(id = Video.Type.TRAILER.stringRes)
            )

            if (isExpanded) {
                Video.Type.values().filter { it != Video.Type.TRAILER }.forEach { type ->
                    VideoGroup(
                        results = results,
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
            modifier = Modifier.padding(start = 8.dp, top = 8.dp)
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
private fun ReviewsCard(
    itemId: Int?,
    service: DetailService,
    modifier: Modifier = Modifier
) {
    val reviewsResponse = remember { mutableStateOf<ReviewResponse?>(null) }
    itemId?.let {
        if (reviewsResponse.value == null) {
            fetchReviews(itemId, service, reviewsResponse)
        }
    }

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
       val reviews = reviewsResponse.value?.results ?: emptyList()
       if (reviews.isNotEmpty()) {
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

private fun fetchMediaItem(id: Int, service: DetailService, mediaItem: MutableState<DetailedItem?>) {
    CoroutineScope(Dispatchers.IO).launch {
        val response = service.getById(id)
        if (response.isSuccessful) {
            withContext(Dispatchers.Main) {
                mediaItem.value = response.body()
            }
        }
    }
}

fun fetchImages(id: Int, service: DetailService, images: MutableState<ImageCollection?>) {
    CoroutineScope(Dispatchers.IO).launch {
        val response = service.getImages(id)
        if (response.isSuccessful) {
            withContext(Dispatchers.Main) {
                images.value = response.body()
            }
        }
    }
}

private fun fetchCastAndCrew(id: Int, service: DetailService, castAndCrew: MutableState<CastAndCrew?>) {
    CoroutineScope(Dispatchers.IO).launch {
        val response = service.getCastAndCrew(id)
        if (response.isSuccessful) {
            withContext(Dispatchers.Main) {
                castAndCrew.value = response.body()
            }
        }
    }
}

private fun fetchMovieContentRating(id: Int, service: MoviesService, contentRating: MutableState<String>) {
    CoroutineScope(Dispatchers.IO).launch {
        val results = service.getReleaseDates(id)
        if (results.isSuccessful) {
            val cr = TmdbUtils.getMovieRating(results.body())
            withContext(Dispatchers.Main) {
                contentRating.value = cr
            }
        }
    }
}

private fun fetchTvContentRating(id: Int, service: TvService, contentRating: MutableState<String>) {
    CoroutineScope(Dispatchers.IO).launch {
        val results = service.getContentRatings(id)
        if (results.isSuccessful) {
            val cr = TmdbUtils.getTvRating(results.body())
            withContext(Dispatchers.Main) {
                contentRating.value = cr
            }
        }
    }
}

private fun fetchSimilarContent(id: Int, service: DetailService, similarContent: MutableState<HomePageResponse?>) {
    CoroutineScope(Dispatchers.IO).launch {
        val results = service.getSimilar(id, 1)
        if (results.isSuccessful) {
            withContext(Dispatchers.Main) {
                similarContent.value = results.body()
            }
        }
    }
}

private fun fetchVideos(id: Int, service: DetailService, videoResponse: MutableState<VideoResponse?>) {
    CoroutineScope(Dispatchers.IO).launch {
        val results = service.getVideos(id)
        if (results.isSuccessful) {
            withContext(Dispatchers.Main) {
                videoResponse.value = results.body()
            }
        }
    }
}

private fun fetchReviews(id: Int, service: DetailService, reviewResponse: MutableState<ReviewResponse?>) {
    CoroutineScope(Dispatchers.IO).launch {
        val result = service.getReviews(id)
        if (result.isSuccessful) {
            withContext(Dispatchers.Main) {
                reviewResponse.value = result.body()
            }
        }
    }
}

private fun fetchKeywords(id: Int, service: DetailService, keywordsResponse: MutableState<KeywordsResponse?>) {
    CoroutineScope(Dispatchers.IO).launch {
        val result = service.getKeywords(id)
        if (result.isSuccessful) {
            withContext(Dispatchers.Main) {
                keywordsResponse.value = result.body()
            }
        }
    }
}

private fun postRating(context: Context, rating: Float, itemId: Int, service: DetailService, itemIsRated: MutableState<Boolean>) {
    CoroutineScope(Dispatchers.IO).launch {
        val response = service.postRating(itemId, RatingBody(rating = rating))
        if (response.isSuccessful) {
            SessionManager.currentSession.value?.refresh(changed = arrayOf(SessionManager.Session.Changed.RatedMovies, SessionManager.Session.Changed.RatedTv))
            withContext(Dispatchers.Main) {
                itemIsRated.value = true
            }
        } else {
            withContext(Dispatchers.Main) {
                val errorObj = JSONObject(response.errorBody().toString())
                Toast.makeText(context, "Error: ${errorObj.getString("status_message")}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

private fun deleteRating(context: Context, itemId: Int, service: DetailService, itemIsRated: MutableState<Boolean>) {
    CoroutineScope(Dispatchers.IO).launch {
        val response = service.deleteRating(itemId)
        if (response.isSuccessful) {
            SessionManager.currentSession.value?.refresh(changed = SessionManager.Session.Changed.Rated)
            withContext(Dispatchers.Main) {
                itemIsRated.value = false
            }
        } else {
            withContext(Dispatchers.Main) {
                val errorObj = JSONObject(response.errorBody().toString())
                Toast.makeText(context, "Error: ${errorObj.getString("status_message")}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

private fun addToWatchlist(
    context: Context,
    itemId: Int,
    type: MediaViewType,
    itemIsWatchlisted: MutableState<Boolean>,
    onWatchlistChanged: (Boolean) -> Unit
) {
    val currentSession = SessionManager.currentSession.value
    val accountId = currentSession!!.accountDetails.value!!.id
    CoroutineScope(Dispatchers.IO).launch {
        val response = AccountService().addToWatchlist(accountId, WatchlistBody(type, itemId, !itemIsWatchlisted.value))
        if (response.isSuccessful) {
            currentSession.refresh(changed = SessionManager.Session.Changed.Watchlist)
            withContext(Dispatchers.Main) {
                itemIsWatchlisted.value = !itemIsWatchlisted.value
                onWatchlistChanged(itemIsWatchlisted.value)
            }
        } else {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

private fun mediaAddToFavorite(
    context: Context,
    itemId: Int,
    type: MediaViewType,
    itemIsFavorited: MutableState<Boolean>,
    onFavoriteChanged: (Boolean) -> Unit
) {
    val currentSession = SessionManager.currentSession.value
    val accountId = currentSession!!.accountDetails.value!!.id
    CoroutineScope(Dispatchers.IO).launch {
        val response = AccountService().markAsFavorite(accountId, MarkAsFavoriteBody(type, itemId, !itemIsFavorited.value))
        if (response.isSuccessful) {
            currentSession.refresh(changed = SessionManager.Session.Changed.Favorites)
            withContext(Dispatchers.Main) {
                itemIsFavorited.value = !itemIsFavorited.value
                onFavoriteChanged(itemIsFavorited.value)
            }
        }
    }
}

object DetailsTab: TabNavItem("details_route") {
    override val name: String
    get() = "Details"
}
object SeasonsTab: TabNavItem("seasons_route") {
    override val name: String
        get() = "Seasons"
}