package com.owenlejeune.tvtime.api.tmdb

import com.owenlejeune.tvtime.api.tmdb.model.*
import com.owenlejeune.tvtime.utils.SessionManager
import org.koin.core.component.KoinComponent
import retrofit2.Response

class MoviesService: KoinComponent, DetailService, HomePageService {

    private val movieService by lazy { TmdbClient().createMovieService() }

    override suspend fun getPopular(page: Int): Response<out HomePageResponse> {
        return movieService.getPopularMovies(page)
    }

    override suspend fun getNowPlaying(page: Int): Response<out HomePageResponse> {
        return movieService.getNowPlayingMovies(page)
    }

    override suspend fun getTopRated(page: Int): Response<out HomePageResponse> {
        return movieService.getTopRatedMovies(page)
    }

    override suspend fun getUpcoming(page: Int): Response<out HomePageResponse> {
        return movieService.getUpcomingMovies(page)
    }

    suspend fun getReleaseDates(id: Int): Response<MovieReleaseResults> {
        return movieService.getReleaseDates(id)
    }

    override suspend fun getById(id: Int): Response<out DetailedItem> {
        return movieService.getMovieById(id)
    }

    override suspend fun getImages(id: Int): Response<ImageCollection> {
        return movieService.getMovieImages(id)
    }

    override suspend fun getCastAndCrew(id: Int): Response<CastAndCrew> {
        return movieService.getCastAndCrew(id)
    }

    override suspend fun getSimilar(id: Int, page: Int): Response<out HomePageResponse> {
        return movieService.getSimilarMovies(id, page)
    }

    override suspend fun getVideos(id: Int): Response<VideoResponse> {
        return movieService.getVideos(id)
    }

    override suspend fun getReviews(id: Int): Response<ReviewResponse> {
        return movieService.getReviews(id)
    }

    override suspend fun postRating(id: Int, rating: RatingBody): Response<RatingResponse> {
        val session = SessionManager.currentSession ?: throw Exception("Session must not be null")
        return if (!session.isAuthorized) {
            movieService.postMovieRatingAsGuest(id, session.sessionId, rating)
        } else {
            movieService.postMovieRatingAsUser(id, session.sessionId, rating)
        }
    }

    override suspend fun deleteRating(id: Int): Response<RatingResponse> {
        val session = SessionManager.currentSession ?: throw Exception("Session must not be null")
        return if (!session.isAuthorized) {
            movieService.deleteMovieReviewAsGuest(id, session.sessionId)
        } else {
            movieService.deleteMovieReviewAsUser(id, session.sessionId)
        }
    }

    override suspend fun getKeywords(id: Int): Response<KeywordsResponse> {
        return movieService.getKeywords(id)
    }

}