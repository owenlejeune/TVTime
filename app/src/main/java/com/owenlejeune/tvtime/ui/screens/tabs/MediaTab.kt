package com.owenlejeune.tvtime.ui.screens.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.ui.components.PagingPosterGrid
import com.owenlejeune.tvtime.ui.components.PillSegmentedControl
import com.owenlejeune.tvtime.ui.components.ScrollableTabs
import com.owenlejeune.tvtime.ui.components.SearchView
import com.owenlejeune.tvtime.ui.navigation.AppNavItem
import com.owenlejeune.tvtime.ui.viewmodel.HomeScreenViewModel
import com.owenlejeune.tvtime.ui.viewmodel.MainViewModel
import com.owenlejeune.tvtime.utils.types.MediaViewType
import com.owenlejeune.tvtime.utils.types.TimeWindow

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MediaTab(
    appNavController: NavHostController,
    mediaType: MediaViewType
) {
    val homeScreenViewModel = viewModel<HomeScreenViewModel>()
    val titleText = when (mediaType) {
        MediaViewType.MOVIE -> stringResource(id = R.string.nav_movies_title)
        MediaViewType.TV -> stringResource(id = R.string.nav_tv_title)
        else -> ""
    }
    homeScreenViewModel.appBarTitle.value = titleText

    Column {
        SearchView(
            title = titleText,
            appNavController = appNavController,
            mediaType = mediaType
        )

        val tabs = MediaTabNavItem.itemsForType(type = mediaType)
        val pagerState = rememberPagerState()
        ScrollableTabs(tabs = tabs, pagerState = pagerState)
        MediaTabs(
            tabs = tabs,
            pagerState = pagerState,
            appNavController = appNavController,
            mediaViewType = mediaType
        )
    }
}

@Composable
fun MediaTabContent(
    appNavController: NavHostController,
    mediaType: MediaViewType,
    mediaTabItem: MediaTabNavItem
) {
    val viewModel = viewModel<MainViewModel>()
    val mediaListItems = viewModel.produceMediaTabFlowFor(mediaType, mediaTabItem.type).collectAsLazyPagingItems()

    PagingPosterGrid(
        lazyPagingItems = mediaListItems,
        onClick = { id ->
            appNavController.navigate(
                AppNavItem.DetailView.withArgs(mediaType, id)
            )
        }
    )
}

@Composable
fun MediaTabTrendingContent(
    appNavController: NavHostController,
    mediaType: MediaViewType,
    mediaTabItem: MediaTabNavItem
) {
    val viewModel = viewModel<MainViewModel>()

    val timeWindow = remember { mutableStateOf(TimeWindow.DAY) }
    val flow = remember { mutableStateOf(viewModel.produceTrendingFor(mediaType, timeWindow.value)) }

    LaunchedEffect(timeWindow.value) {
        flow.value = viewModel.produceTrendingFor(mediaType, timeWindow.value)
    }

    val mediaListItems = flow.value.collectAsLazyPagingItems()

    PagingPosterGrid(
        lazyPagingItems = mediaListItems,
        header = {
            val options = listOf(TimeWindow.DAY, TimeWindow.WEEK)

            val context = LocalContext.current
            PillSegmentedControl(
                items = options,
                itemLabel = { _, i ->
                    when (i) {
                        TimeWindow.DAY -> context.getString(R.string.time_window_day)
                        TimeWindow.WEEK -> context.getString(R.string.time_window_week)
                    }
                },
                onItemSelected = { _, i -> timeWindow.value = i },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
                    .padding(bottom = 4.dp)
            )
        },
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