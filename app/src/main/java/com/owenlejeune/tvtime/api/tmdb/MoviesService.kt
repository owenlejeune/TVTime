package com.owenlejeune.tvtime.api.tmdb

import org.koin.core.component.KoinComponent

class MoviesService: KoinComponent {

    private val service by lazy { TmdbClient().createMovieService() }

    suspend fun getPopularMovies(page: Int = 1) = service.getPopularMovies(page)

}