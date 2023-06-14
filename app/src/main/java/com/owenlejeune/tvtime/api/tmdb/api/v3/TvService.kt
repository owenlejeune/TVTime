package com.owenlejeune.tvtime.api.tmdb.api.v3

import com.owenlejeune.tvtime.api.tmdb.TmdbClient
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.CastAndCrew
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.DetailedItem
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.HomePageResponse
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.ImageCollection
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.KeywordsResponse
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.RatingBody
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.ReviewResponse
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.Season
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.StatusResponse
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.TvContentRatings
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.VideoResponse
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.WatchProviderResponse
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

    override suspend fun postRating(id: Int, ratingBody: RatingBody): Response<StatusResponse> {
        val session = SessionManager.currentSession.value ?: throw Exception("Session must not be null")
        return service.postTvRatingAsUser(id, session.sessionId, ratingBody)
    }

    override suspend fun deleteRating(id: Int): Response<StatusResponse> {
        val session = SessionManager.currentSession.value ?: throw Exception("Session must not be null")
        return service.deleteTvReviewAsUser(id, session.sessionId)
    }

    override suspend fun getKeywords(id: Int): Response<KeywordsResponse> {
        return service.getKeywords(id)
    }

    override suspend fun getWatchProviders(id: Int): Response<WatchProviderResponse> {
        return service.getWatchProviders(id)
    }

    suspend fun getSeason(seriesId: Int, seasonId: Int): Response<Season> {
        return service.getSeason(seriesId, seasonId)
    }

}