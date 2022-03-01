package com.owenlejeune.tvtime.ui.screens.tabs.bottom

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.owenlejeune.tvtime.api.tmdb.HomePageService
import com.owenlejeune.tvtime.api.tmdb.MoviesService
import com.owenlejeune.tvtime.api.tmdb.TvService
import com.owenlejeune.tvtime.ui.components.PosterGrid
import com.owenlejeune.tvtime.ui.navigation.MainNavItem
import com.owenlejeune.tvtime.ui.navigation.MediaFetchFun
import com.owenlejeune.tvtime.ui.navigation.MediaTabNavItem
import com.owenlejeune.tvtime.ui.screens.MediaViewType
import com.owenlejeune.tvtime.ui.screens.tabs.top.Tabs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MediaTab(appNavController: NavHostController, mediaType: MediaViewType) {
    Column {
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
fun MediaTabContent(appNavController: NavHostController, mediaType: MediaViewType, mediaFetchFun: MediaFetchFun) {
    val service: HomePageService = when(mediaType) {
        MediaViewType.MOVIE -> MoviesService()
        MediaViewType.TV -> TvService()
        else -> throw IllegalArgumentException("Media type given: ${mediaType}, \n     expected one of MediaViewType.MOVIE, MediaViewType.TV") // shouldn't happen
    }
    PosterGrid(
        fetchMedia =  { mediaList ->
            CoroutineScope(Dispatchers.IO).launch {
                val response = mediaFetchFun.invoke(service, 1)
                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        mediaList.value = response.body()?.results ?: emptyList()
                    }
                }
            }
        },
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
        tabs[page].screen(appNavController, mediaViewType, tabs[page].mediaFetchFun)
    }
}

@OptIn(ExperimentalPagerApi::class)
@Preview(showBackground = true)
@Composable
fun MediaTabsPreview() {
    val tabs = MediaTabNavItem.MovieItems
    val pagerState = rememberPagerState()
    MediaTabs(tabs = tabs, pagerState = pagerState, MediaViewType.MOVIE)
}

//    val moviesViewModel = viewModel(PopularMovieViewModel::class.java)
//    val moviesList = moviesViewModel.moviePage
//    val movieListItems: LazyPagingItems<PopularMovie> = moviesList.collectAsLazyPagingItems()