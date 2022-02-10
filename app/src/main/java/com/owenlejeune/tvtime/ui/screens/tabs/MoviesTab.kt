package com.owenlejeune.tvtime.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import com.owenlejeune.tvtime.api.tmdb.MoviesService
import com.owenlejeune.tvtime.ui.components.PosterGrid

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MoviesTab() {
//    val moviesViewModel = viewModel(PopularMovieViewModel::class.java)
//    val moviesList = moviesViewModel.moviePage
//    val movieListItems: LazyPagingItems<PopularMovie> = moviesList.collectAsLazyPagingItems()
    PosterGrid { moviesList ->
        val service = MoviesService()
        service.getPopularMovies { isSuccessful, response ->
            if (isSuccessful) {
                moviesList.value = response!!.movies
            }
        }
    }
}