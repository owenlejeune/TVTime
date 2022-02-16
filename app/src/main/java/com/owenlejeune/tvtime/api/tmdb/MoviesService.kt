package com.owenlejeune.tvtime.api.tmdb

import com.owenlejeune.tvtime.api.tmdb.model.*
import org.koin.core.component.KoinComponent
import retrofit2.Response

class MoviesService: KoinComponent, DetailService {

    private val service by lazy { TmdbClient().createMovieService() }

    suspend fun getPopularMovies(page: Int = 1): Response<PopularMoviesResponse> {
        return service.getPopularMovies(page)
    }

    suspend fun getReleaseDates(id: Int): Response<MovieReleaseResults> {
        return service.getReleaseDates(id)
    }

    override suspend fun getById(id: Int): Response<out DetailedItem> {
        return service.getMovieById(id)
    }

    override suspend fun getImages(id: Int): Response<ImageCollection> {
        return service.getMovieImages(id)
    }

    override suspend fun getCastAndCrew(id: Int): Response<CastAndCrew> {
        return service.getCastAndCrew(id)
    }

}