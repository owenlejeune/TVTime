package com.owenlejeune.tvtime.api.tmdb

import com.owenlejeune.tvtime.api.tmdb.model.DetailedItem
import org.koin.core.component.KoinComponent
import retrofit2.Response

class MoviesService: KoinComponent, DetailService {

    private val service by lazy { TmdbClient().createMovieService() }

    suspend fun getPopularMovies(page: Int = 1) = service.getPopularMovies(page)

    override suspend fun getById(id: Int): Response<DetailedItem> = service.getMovieById(id) as Response<DetailedItem>

}