package com.owenlejeune.tvtime.ui.screens.main

import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.*
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.V4AccountList
import com.owenlejeune.tvtime.api.tmdb.viewmodel.RecommendedMediaViewModel
import com.owenlejeune.tvtime.extensions.unlessEmpty
import com.owenlejeune.tvtime.ui.components.AccountIcon
import com.owenlejeune.tvtime.ui.components.PagingPosterGrid
import com.owenlejeune.tvtime.ui.components.ProfileMenuContainer
import com.owenlejeune.tvtime.ui.components.ProfileMenuOverlay
import com.owenlejeune.tvtime.ui.navigation.AccountTabNavItem
import com.owenlejeune.tvtime.ui.navigation.ListFetchFun
import com.owenlejeune.tvtime.ui.navigation.MainNavItem
import com.owenlejeune.tvtime.ui.screens.main.tabs.top.ScrollableTabs
import com.owenlejeune.tvtime.utils.SessionManager
import com.owenlejeune.tvtime.utils.TmdbUtils
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountView(
    appNavController: NavHostController,
    doSignInPartTwo: Boolean = false
) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setStatusBarColor(color = MaterialTheme.colorScheme.background)
    systemUiController.setNavigationBarColor(color = MaterialTheme.colorScheme.background)

    val currentSessionState = remember { SessionManager.currentSession }
    val currentSession = currentSessionState.value

    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val topAppBarScrollState = rememberTopAppBarScrollState()
    val scrollBehavior = remember(decayAnimationSpec) {
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(decayAnimationSpec, topAppBarScrollState)
    }
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(
                        onClick = { appNavController.popBackStack() }
                    ) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                title = {
                    if (currentSession?.isAuthorized == false) {
                        Text(text = stringResource(id = R.string.account_not_logged_in))
                    } else {
                        val accountDetails = remember { currentSession!!.accountDetails }
                        Text(text = getAccountName(accountDetails.value))
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(scrolledContainerColor = MaterialTheme.colorScheme.background),
                actions = {
                    var showDropDownMenu by remember { mutableStateOf(false) }
                    AccountIcon(
                        modifier = Modifier.padding(end = 8.dp),
                        size = 32.dp,
                        onClick = { showDropDownMenu = true }
                    )
                    DropdownMenu(
                        expanded = showDropDownMenu,
                        onDismissRequest = { showDropDownMenu = false}
                    ) {
                        DropdownMenuItem(
                            text = { Text(text = stringResource(id = R.string.action_sign_out)) },
                            onClick = {
                                SessionManager.clearSession()
                                appNavController.popBackStack()
                            }
                        )
                    }
                }
            )
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            AccountViewContent(appNavController = appNavController, doSignInPartTwo = doSignInPartTwo)
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun AccountViewContent(
    appNavController: NavHostController,
    doSignInPartTwo: Boolean = false
) {
    val currentSessionState = remember { SessionManager.currentSession }
    val currentSession = currentSessionState.value
    val scope = rememberCoroutineScope()

    if (currentSession?.isAuthorized != true && doSignInPartTwo) {
        AccountLoadingView()
        LaunchedEffect(Unit) {
            scope.launch {
                SessionManager.signInPart2()
            }
        }
    } else {
        currentSession?.let {
            Column {
                val tabs = AccountTabNavItem.AuthorizedItems
                val pagerState = rememberPagerState()
                ScrollableTabs(tabs = tabs, pagerState = pagerState)
                AccountTabs(
                    appNavController = appNavController,
                    tabs = tabs,
                    pagerState = pagerState
                )
            }
        }
    }
}

@Composable
private fun AccountLoadingView() {
    Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(
            modifier = Modifier
                .align(Alignment.Center)
                .size(200.dp),
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

private fun getAccountName(accountDetails: AccountDetails?): String {
    if (accountDetails != null) {
        if (accountDetails.name.isNotEmpty()) {
            return accountDetails.name
        } else if (accountDetails.username.isNotEmpty()) {
            return accountDetails.username
        }
    }
    return ""
}

@Composable
fun <T: Any> AccountTabContent(
    noContentText: String,
    appNavController: NavHostController,
    mediaViewType: MediaViewType,
    listFetchFun: ListFetchFun,
    clazz: KClass<T>
) {
    val contentItems = remember { listFetchFun() }

    if (contentItems.isEmpty()) {
        Column {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = noContentText,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 22.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(contentItems.size) { i ->
                when (clazz) {
                    RatedTv::class, RatedMovie::class -> {
                        val item = contentItems[i] as RatedTopLevelMedia
                        MediaItemRow(
                            appNavController = appNavController,
                            mediaViewType = mediaViewType,
                            id = item.id,
                            posterPath = TmdbUtils.getFullPosterPath(item.posterPath),
                            backdropPath = TmdbUtils.getFullBackdropPath(item.backdropPath),
                            name = item.name,
                            date = item.releaseDate,
                            rating = item.rating,
                            description = item.overview
                        )
                    }
                    RatedEpisode::class -> {
                        val item = contentItems[i] as RatedMedia
                        MediaItemRow(
                            appNavController = appNavController,
                            mediaViewType = mediaViewType,
                            id = item.id,
                            name = item.name,
                            date = item.releaseDate,
                            rating = item.rating,
                            description = item.overview
                        )
                    }
                    FavoriteMovie::class, FavoriteTvSeries::class -> {
                        val item = contentItems[i] as FavoriteMedia
                        MediaItemRow(
                            appNavController = appNavController,
                            mediaViewType = mediaViewType,
                            id = item.id,
                            posterPath = TmdbUtils.getFullPosterPath(item.posterPath),
                            backdropPath = TmdbUtils.getFullBackdropPath(item.backdropPath),
                            name = item.title,
                            date = item.releaseDate,
                            description = item.overview
                        )
                    }
                    WatchlistMovie::class, WatchlistTvSeries::class -> {
                        val item = contentItems[i] as WatchlistMedia
                        MediaItemRow(
                            appNavController = appNavController,
                            mediaViewType = mediaViewType,
                            id = item.id,
                            posterPath = TmdbUtils.getFullPosterPath(item.posterPath),
                            backdropPath = TmdbUtils.getFullBackdropPath(item.backdropPath),
                            name = item.title,
                            date = item.releaseDate,
                            description = item.overview
                        )
                    }
                    V4AccountList::class -> {
                        val item = contentItems[i] as V4AccountList
                        MediaItemRow(
                            appNavController = appNavController,
                            mediaViewType = mediaViewType,
                            id = item.id,
                            name = item.name,
                            date = item.createdAt,
                            description = item.description.unlessEmpty(stringResource(R.string.no_description_provided)),
                            posterPath = TmdbUtils.getFullPosterPath(item.posterPath),
                            backdropPath = TmdbUtils.getFullBackdropPath(item.backdropPath)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecommendedAccountTabContent(
    noContentText: String,
    appNavController: NavHostController,
    mediaViewType: MediaViewType,
) {
    val viewModel = when (mediaViewType) {
        MediaViewType.MOVIE -> RecommendedMediaViewModel.RecommendedMoviesVM
        MediaViewType.TV -> RecommendedMediaViewModel.RecommendedTvVM
        else -> throw IllegalArgumentException("Media type given: ${mediaViewType}, \n     expected one of MediaViewType.MOVIE, MediaViewType.TV") // shouldn't happen
    }
    val mediaListItems = viewModel.mediaItems.collectAsLazyPagingItems()

    if (mediaListItems.itemCount < 1) {
        Column {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = noContentText,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 22.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    } else {
        PagingPosterGrid(
            lazyPagingItems = mediaListItems,
            onClick = { id ->
                appNavController.navigate(
                    "${MainNavItem.DetailView.route}/${mediaViewType}/${id}"
                )
            }
        )
    }
}

@Composable
private fun MediaItemRow(
    appNavController: NavHostController,
    mediaViewType: MediaViewType,
    id: Int,
    name: String,
    date: String,
    description: String,
    posterPath: String? = null,
    backdropPath: String? = null,
    rating: Float? = null
) {
    MediaResultCard(
        appNavController = appNavController,
        mediaViewType = mediaViewType,
        id = id,
        backdropPath = backdropPath,
        posterPath = posterPath,
        title = "$name (${date.split("-")[0]})",
        additionalDetails = listOf(description),
        rating = rating
    )
}


@OptIn(ExperimentalPagerApi::class)
@Composable
fun AccountTabs(
    tabs: List<AccountTabNavItem>,
    pagerState: PagerState,
    appNavController: NavHostController
) {
    HorizontalPager(count = tabs.size, state = pagerState) { page ->
        tabs[page].screen(tabs[page].noContentText, appNavController, tabs[page].mediaType, tabs[page].listFetchFun, tabs[page].listType)
    }
}