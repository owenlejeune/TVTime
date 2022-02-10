package com.owenlejeune.tvtime.ui.screens

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.api.tmdb.MoviesService
import com.owenlejeune.tvtime.api.tmdb.model.PopularMovie
import com.owenlejeune.tvtime.extensions.dpToPx
import com.owenlejeune.tvtime.extensions.items

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MoviesTab() {
    val context = LocalContext.current
//    val moviesViewModel = viewModel(PopularMovieViewModel::class.java)
//    val moviesList = moviesViewModel.moviePage
//    val movieListItems: LazyPagingItems<PopularMovie> = moviesList.collectAsLazyPagingItems()
    val moviesList = remember { mutableStateOf(emptyList<PopularMovie>()) }
    val service = MoviesService()
    service.getPopularMovies { isSuccessful, response ->
        if (isSuccessful) {
            moviesList.value = response!!.movies
        }
    }

    LazyVerticalGrid(
        cells = GridCells.Fixed(count = 3),
        contentPadding = PaddingValues(8.dp)
    ) {
//        items(movieListItems) { item ->
        items(moviesList.value) { item ->
            val poster = item?.let { i ->
                "https://image.tmdb.org/t/p/original${i.posterPath}"
            }
            Image(
                painter = rememberImagePainter(
                    data = poster,
                    builder = {
                        transformations(RoundedCornersTransformation(5f.dpToPx(context)))
                        placeholder(R.drawable.placeholder)
                    }
                ),
                contentDescription = item?.title,
                modifier = Modifier
                    .size(190.dp)
                    .padding(5.dp)
                    .clickable {
                        Toast.makeText(context, "${item?.title} clicked", Toast.LENGTH_SHORT).show()
                    }
            )
        }
    }
}