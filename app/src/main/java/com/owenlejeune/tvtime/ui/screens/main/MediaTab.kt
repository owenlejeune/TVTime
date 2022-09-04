package com.owenlejeune.tvtime.ui.screens.main

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.api.tmdb.api.v3.HomePageService
import com.owenlejeune.tvtime.api.tmdb.api.v3.MoviesService
import com.owenlejeune.tvtime.api.tmdb.api.v3.TvService
import com.owenlejeune.tvtime.preferences.AppPreferences
import com.owenlejeune.tvtime.ui.components.PagingPosterGrid
import com.owenlejeune.tvtime.ui.components.PosterGrid
import com.owenlejeune.tvtime.ui.components.SearchBar
import com.owenlejeune.tvtime.ui.components.SearchView
import com.owenlejeune.tvtime.ui.navigation.MainNavItem
import com.owenlejeune.tvtime.ui.navigation.MediaFetchFun
import com.owenlejeune.tvtime.ui.navigation.MediaTabNavItem
import com.owenlejeune.tvtime.ui.navigation.MediaTabViewModel
import com.owenlejeune.tvtime.ui.screens.main.tabs.top.Tabs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.get

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MediaTab(
    appBarTitle: MutableState<String>,
    appNavController: NavHostController,
    mediaType: MediaViewType,
    fab: MutableState<@Composable () -> Unit>
) {
    appBarTitle.value = when (mediaType) {
        MediaViewType.MOVIE -> stringResource(id = R.string.nav_movies_title)
        MediaViewType.TV -> stringResource(id = R.string.nav_tv_title)
        else -> ""
    }

    Column {
        SearchView(
            title = appBarTitle.value,
            appNavController = appNavController,
            mediaType = mediaType,
            fab = fab
        )

        val tabs = when (mediaType) {
            MediaViewType.MOVIE -> MediaTabNavItem.MovieItems
            MediaViewType.TV -> MediaTabNavItem.TvItems
            else -> throw IllegalArgumentException("Media type given: ${mediaType}, \n     expected one of MediaViewType.MOVIE, MediaViewType.TV") // shouldn't happen
        }
        val pagerState = rememberPagerState()
        Tabs(tabs = tabs, pagerState = pagerState)
        MediaTabs(
            tabs = tabs,
            pagerState = pagerState,
            appNavController = appNavController,
            mediaViewType = mediaType
        )
    }
}

@Composable
fun MediaTabContent(appNavController: NavHostController, mediaType: MediaViewType, mediaTabItem: MediaTabNavItem) {
    val viewModel: MediaTabViewModel? = when(mediaType) {
        MediaViewType.MOVIE -> mediaTabItem.movieViewModel
        MediaViewType.TV -> mediaTabItem.tvViewModel
        else -> throw IllegalArgumentException("Media type given: ${mediaType}, \n     expected one of MediaViewType.MOVIE, MediaViewType.TV") // shouldn't happen
    }
    val mediaListItems = viewModel?.mediaItems?.collectAsLazyPagingItems()

    PagingPosterGrid(
        lazyPagingItems = mediaListItems,
        onClick = { id ->
            appNavController.navigate(
                "${MainNavItem.DetailView.route}/${mediaType}/${id}"
            )
        }
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MediaTabs(
    tabs: List<MediaTabNavItem>,
    pagerState: PagerState,
    mediaViewType: MediaViewType,
    appNavController: NavHostController = rememberNavController()
) {
    HorizontalPager(count = tabs.size, state = pagerState) { page ->
        tabs[page].screen(appNavController, mediaViewType, tabs[page])
    }
}

//@Composable
//private fun SearchView(
//    title: String,
//    appNavController: NavHostController,
//    mediaType: MediaViewType,
//    fab: MutableState<@Composable () -> Unit>,
//    preferences: AppPreferences = get(AppPreferences::class.java)
//) {
//    val route = "${MainNavItem.SearchView.route}/${mediaType.ordinal}"
//    if (preferences.showSearchBar) {
//        SearchBar(
//            placeholder = title
//        ) {
//            appNavController.navigate(route)
//        }
//    } else {
//        fab.value = @Composable {
//            FloatingActionButton(
//                onClick = {
//                    appNavController.navigate(route)
//                }
//            ) {
//                Icon(Icons.Filled.Search, stringResource(id = R.string.preference_heading_search))
//            }
//        }
//    }
//}