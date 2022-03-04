package com.owenlejeune.tvtime.api.tmdb

import com.owenlejeune.tvtime.api.tmdb.model.*
import com.owenlejeune.tvtime.utils.SessionManager
import org.koin.core.component.KoinComponent
import retrofit2.Response

class TvService: KoinComponent, DetailService, HomePageService {

    private val service by lazy { TmdbClient().createTvService() }

    override suspend fun getPopular(page: Int): Response<out HomePageResponse> {
        return service.getPoplarTv(page)
    }

    override suspend fun getNowPlaying(page: Int): Response<out HomePageResponse> {
        return service.getTvAiringToday(page)
    }

    override suspend fun getTopRated(page: Int): Response<out HomePageResponse> {
        return service.getTopRatedTv(page)
    }

    override suspend fun getUpcoming(page: Int): Response<out HomePageResponse> {
        return service.getTvOnTheAir(page)
    }

    override suspend fun getById(id: Int): Response<out DetailedItem> {
        return service.getTvShowById(id)
    }

    override suspend fun getImages(id: Int): Response<ImageCollection> {
        return service.getTvImages(id)
    }

    override suspend fun getCastAndCrew(id: Int): Response<CastAndCrew> {
        return service.getCastAndCrew(id)
    }

    suspend fun getContentRatings(id: Int): Response<TvContentRatings> {
        return service.getContentRatings(id)
    }

    override suspend fun getSimilar(id: Int, page: Int): Response<out HomePageResponse> {
        return service.getSimilarTvShows(id, page)
    }

    override suspend fun getVideos(id: Int): Response<VideoResponse> {
        return service.getVideos(id)
    }

    override suspend fun getReviews(id: Int): Response<ReviewResponse> {
        return service.getReviews(id)
    }

    override suspend fun postRating(id: Int, rating: RatingBody): Response<RatingResponse> {
        val session = SessionManager.currentSession ?: throw Exception("Session must not be null")
        return if (session.isGuest) {
            service.postTvRatingAsGuest(id, session.sessionId, rating)
        } else {
            service.postTvRatingAsUser(id, session.sessionId, rating)
        }
    }

    override suspend fun deleteRating(id: Int): Response<RatingResponse> {
        val session = SessionManager.currentSession ?: throw Exception("Session must not be null")
        return if (session.isGuest) {
            service.deleteTvReviewAsGuest(id, session.sessionId)
        } else {
            service.deleteTvReviewAsUser(id, session.sessionId)
        }
    }

    override suspend fun getKeywords(id: Int): Response<KeywordsResponse> {
        return service.getKeywords(id)
    }

}