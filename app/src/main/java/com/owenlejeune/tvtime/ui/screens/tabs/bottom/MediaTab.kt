package com.owenlejeune.tvtime.ui.screens.tabs.bottom

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import com.owenlejeune.tvtime.api.tmdb.HomePageService
import com.owenlejeune.tvtime.api.tmdb.MoviesService
import com.owenlejeune.tvtime.api.tmdb.TvService
import com.owenlejeune.tvtime.ui.components.PosterGrid
import com.owenlejeune.tvtime.ui.navigation.MainNavItem
import com.owenlejeune.tvtime.ui.navigation.MainTabNavItem
import com.owenlejeune.tvtime.ui.navigation.MediaFetchFun
import com.owenlejeune.tvtime.ui.screens.MediaViewType
import com.owenlejeune.tvtime.ui.screens.tabs.top.Tabs
import com.owenlejeune.tvtime.ui.screens.tabs.top.TabsContent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MediaTab(appNavController: NavHostController, mediaType: MediaViewType) {
    Column {
        val tabs = when (mediaType) {
            MediaViewType.MOVIE -> MainTabNavItem.MovieItems
            MediaViewType.TV -> MainTabNavItem.TvItems
        }
        val pagerState = rememberPagerState()
        Tabs(tabs = tabs, pagerState = pagerState)
        TabsContent(
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

//    val moviesViewModel = viewModel(PopularMovieViewModel::class.java)
//    val moviesList = moviesViewModel.moviePage
//    val movieListItems: LazyPagingItems<PopularMovie> = moviesList.collectAsLazyPagingItems()