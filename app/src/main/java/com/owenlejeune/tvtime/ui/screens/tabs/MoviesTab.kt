package com.owenlejeune.tvtime.ui.screens.tabs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.owenlejeune.tvtime.api.tmdb.MoviesService
import com.owenlejeune.tvtime.ui.components.PosterGrid
import com.owenlejeune.tvtime.ui.screens.DetailViewType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MoviesTab(appNavController: NavController) {
//    val moviesViewModel = viewModel(PopularMovieViewModel::class.java)
//    val moviesList = moviesViewModel.moviePage
//    val movieListItems: LazyPagingItems<PopularMovie> = moviesList.collectAsLazyPagingItems()
    PosterGrid(appNavController = appNavController, type = DetailViewType.MOVIE) { moviesList ->
        val service = MoviesService()
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.getPopularMovies()
            if (response.isSuccessful) {
                withContext(Dispatchers.Main) {
                    moviesList.value = response.body()!!.movies
                }
            }
        }
    }
}