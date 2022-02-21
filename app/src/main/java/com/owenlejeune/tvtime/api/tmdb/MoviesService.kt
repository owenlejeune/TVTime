package com.owenlejeune.tvtime.api.tmdb

import com.owenlejeune.tvtime.api.tmdb.model.*
import org.koin.core.component.KoinComponent
import retrofit2.Response

class MoviesService: KoinComponent, DetailService, HomePageService {

    private val service by lazy { TmdbClient().createMovieService() }

    override suspend fun getPopular(page: Int): Response<out HomePageResponse> {
        return service.getPopularMovies(page)
    }

    override suspend fun getNowPlaying(page: Int): Response<out HomePageResponse> {
        return service.getNowPlayingMovies(page)
    }

    override suspend fun getTopRated(page: Int): Response<out HomePageResponse> {
        return service.getTopRatedMovies(page)
    }

    override suspend fun getUpcoming(page: Int): Response<out HomePageResponse> {
        return service.getUpcomingMovies(page)
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

    override suspend fun getSimilar(id: Int, page: Int): Response<out HomePageResponse> {
        return service.getSimilarMovies(id, page)
    }

    override suspend fun getVideos(id: Int): Response<VideoResponse> {
        return service.getVideos(id)
    }

}