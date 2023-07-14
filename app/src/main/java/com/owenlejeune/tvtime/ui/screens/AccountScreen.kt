package com.owenlejeune.tvtime.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.*
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.AccountList
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.RatedMedia
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.RatedMovie
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.RatedTv
import com.owenlejeune.tvtime.extensions.getCalendarYear
import com.owenlejeune.tvtime.extensions.lazyPagingItems
import com.owenlejeune.tvtime.extensions.unlessEmpty
import com.owenlejeune.tvtime.ui.components.AccountIcon
import com.owenlejeune.tvtime.ui.components.BackButton
import com.owenlejeune.tvtime.ui.components.MediaResultCard
import com.owenlejeune.tvtime.ui.components.PagingPosterGrid
import com.owenlejeune.tvtime.ui.components.ScrollableTabs
import com.owenlejeune.tvtime.ui.components.TVTLargeTopAppBar
import com.owenlejeune.tvtime.ui.navigation.AppNavItem
import com.owenlejeune.tvtime.ui.screens.tabs.AccountTabNavItem
import com.owenlejeune.tvtime.ui.viewmodel.AccountViewModel
import com.owenlejeune.tvtime.ui.viewmodel.ApplicationViewModel
import com.owenlejeune.tvtime.utils.SessionManager
import com.owenlejeune.tvtime.utils.TmdbUtils
import com.owenlejeune.tvtime.utils.types.MediaViewType
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.reflect.KClass

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    appNavController: NavHostController,
    doSignInPartTwo: Boolean = false
) {
    val applicationViewModel = viewModel<ApplicationViewModel>()
    applicationViewModel.statusBarColor.value = MaterialTheme.colorScheme.background
    applicationViewModel.navigationBarColor.value = MaterialTheme.colorScheme.background

    val currentSessionState = remember { SessionManager.currentSession }
    val currentSession = currentSessionState.value

    val topAppBarScrollState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarScrollState)

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TVTLargeTopAppBar(
                scrollBehavior = scrollBehavior,
                appNavController = appNavController,
                navigationIcon = {
                    BackButton(navController = appNavController)
                },
                title = {
                    if (currentSession?.isAuthorized == false) {
                        Text(text = stringResource(id = R.string.account_not_logged_in))
                    } else {
                        val accountDetails = remember { currentSession!!.accountDetails }
                        Text(text = getAccountName(accountDetails.value))
                    }
                },
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
    accountTabType: AccountTabNavItem.AccountTabType,
    clazz: KClass<T>
) {
    val accountViewModel = viewModel<AccountViewModel>()
    val contentItems = accountViewModel.getPagingFlowFor(mediaViewType, accountTabType).collectAsLazyPagingItems()

    if (contentItems.itemCount == 0) {
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
    } else if (accountTabType == AccountTabNavItem.AccountTabType.RECOMMENDED) {
        PagingPosterGrid(
            lazyPagingItems = contentItems as LazyPagingItems<TmdbItem>,
            onClick = { id ->
                appNavController.navigate(
                    AppNavItem.DetailView.withArgs(mediaViewType, id)
                )
            }
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            lazyPagingItems(contentItems) { i ->
                when (clazz) {
                    RatedTv::class, RatedMovie::class -> {
                        val item = i as RatedMedia
                        MediaItemRow(
                            appNavController = appNavController,
                            mediaViewType = mediaViewType,
                            id = item.id,
                            posterPath = TmdbUtils.getFullPosterPath(item.posterPath),
                            backdropPath = TmdbUtils.getFullBackdropPath(item.backdropPath),
                            name = item.name,
                            date = item.releaseDate,
                            rating = item.rating.value,
                            description = item.overview
                        )
                    }
                    FavoriteMovie::class, FavoriteTvSeries::class -> {
                        val item = i as FavoriteMedia
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
                        val item = i as WatchlistMedia
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
                    AccountList::class -> {
                        val item = i as AccountList
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
private fun MediaItemRow(
    appNavController: NavHostController,
    mediaViewType: MediaViewType,
    id: Int,
    name: String,
    date: Date?,
    description: String,
    posterPath: String? = null,
    backdropPath: String? = null,
    rating: Float? = null
) {
    var title = "$name •"
    date?.let {
        title += " (${date.getCalendarYear()})"
    }
    MediaResultCard(
        appNavController = appNavController,
        mediaViewType = mediaViewType,
        id = id,
        backdropPath = backdropPath,
        posterPath = posterPath,
        title = title,
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
        tabs[page].screen(tabs[page].noContentText, appNavController, tabs[page].mediaType, tabs[page].type, tabs[page].contentType)
    }
}