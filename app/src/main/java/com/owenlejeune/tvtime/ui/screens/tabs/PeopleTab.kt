package com.owenlejeune.tvtime.ui.screens.tabs

import androidx.compose.foundation.layout.Column
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
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.ui.components.PagingPeoplePosterGrid
import com.owenlejeune.tvtime.ui.components.PagingPosterGrid
import com.owenlejeune.tvtime.ui.components.PillSegmentedControl
import com.owenlejeune.tvtime.ui.components.SearchView
import com.owenlejeune.tvtime.ui.components.Tabs
import com.owenlejeune.tvtime.ui.navigation.AppNavItem
import com.owenlejeune.tvtime.ui.viewmodel.HomeScreenViewModel
import com.owenlejeune.tvtime.ui.viewmodel.MainViewModel
import com.owenlejeune.tvtime.utils.types.MediaViewType
import com.owenlejeune.tvtime.utils.types.TimeWindow

@OptIn(ExperimentalPagerApi::class)
@Composable
fun PeopleTab(
    appNavController: NavHostController,
) {
    val homeScreenViewModel = viewModel<HomeScreenViewModel>()
    homeScreenViewModel.appBarTitle.value = stringResource(id = R.string.popular_today_header)

    Column {
        SearchView(
            title = stringResource(id = R.string.popular_today_header),
            appNavController = appNavController,
            mediaType = MediaViewType.PERSON
        )

        val tabs = PeopleTabNavItem.Items
        val pagerState = rememberPagerState()
        Tabs(tabs = tabs, pagerState = pagerState)
        PeopleTabs(
            tabs = tabs,
            pagerState = pagerState,
            appNavController = appNavController
        )
    }
}

@Composable
fun PopularPeopleContent(
    appNavController: NavController
) {
    val mainViewModel = viewModel<MainViewModel>()
    val peopleList = mainViewModel.popularPeople.collectAsLazyPagingItems()

    PagingPeoplePosterGrid(
        lazyPagingItems = peopleList,
        onClick = { id ->
            appNavController.navigate(
                AppNavItem.DetailView.withArgs(MediaViewType.PERSON, id)
            )
        }
    )
}

@Composable
fun TrendingPeopleContent(
    appNavController: NavController
) {
    val viewModel = viewModel<MainViewModel>()

    val timeWindow = remember { mutableStateOf(TimeWindow.DAY) }
    val flow = remember { mutableStateOf(viewModel.produceTrendingFor(MediaViewType.PERSON, timeWindow.value)) }

    LaunchedEffect(timeWindow.value) {
        flow.value = viewModel.produceTrendingFor(MediaViewType.PERSON, timeWindow.value)
    }

    val mediaListItems = flow.value.collectAsLazyPagingItems()

    Column {
        val timeWindows = listOf(TimeWindow.DAY, TimeWindow.WEEK)
        val selected = remember { mutableStateOf(timeWindows[0]) }

        val context = LocalContext.current
        PillSegmentedControl(
            items = timeWindows,
            itemLabel = { _, i ->
                when (i) {
                    TimeWindow.DAY -> context.getString(R.string.time_window_day)
                    TimeWindow.WEEK -> context.getString(R.string.time_window_week)
                }
            },
            onItemSelected = { _, t ->
                selected.value = t
            },
            modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp)
        )

        PagingPosterGrid(
            lazyPagingItems = mediaListItems,
            onClick = { id ->
                appNavController.navigate(
                    AppNavItem.DetailView.withArgs(MediaViewType.PERSON, id)
                )
            }
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun PeopleTabs(
    tabs: List<PeopleTabNavItem>,
    pagerState: PagerState,
    appNavController: NavHostController = rememberNavController()
) {
    HorizontalPager(count = tabs.size, state = pagerState) { page ->
        tabs[page].screen(appNavController)
    }
}