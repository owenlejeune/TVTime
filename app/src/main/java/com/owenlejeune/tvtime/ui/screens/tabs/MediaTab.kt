package com.owenlejeune.tvtime.ui.screens.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.ui.components.PagingPosterGrid
import com.owenlejeune.tvtime.ui.components.SearchView
import com.owenlejeune.tvtime.ui.navigation.AppNavItem
import com.owenlejeune.tvtime.ui.navigation.MediaTabNavItem
import com.owenlejeune.tvtime.ui.components.Tabs
import com.owenlejeune.tvtime.api.tmdb.viewmodel.MediaTabViewModel
import com.owenlejeune.tvtime.utils.types.MediaViewType

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MediaTab(
    appBarTitle: MutableState<@Composable () -> Unit>,
    appNavController: NavHostController,
    mediaType: MediaViewType,
    fab: MutableState<@Composable () -> Unit>
) {
     val titleText = when (mediaType) {
        MediaViewType.MOVIE -> stringResource(id = R.string.nav_movies_title)
        MediaViewType.TV -> stringResource(id = R.string.nav_tv_title)
        else -> ""
    }
    appBarTitle.value = @Composable { Text(text = titleText) }

    Column {
        SearchView(
            title = titleText,
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
                AppNavItem.DetailView.withArgs(mediaType, id)
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