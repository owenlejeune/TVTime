package com.owenlejeune.tvtime.api.tmdb.api.v3

import com.owenlejeune.tvtime.api.tmdb.api.v3.model.*
import com.owenlejeune.tvtime.utils.types.TimeWindow
import retrofit2.Response
import retrofit2.http.*

interface TvApi {

    @GET("tv/popular")
    suspend fun getPoplarTv(@Query("page") page: Int = 1): Response<HomePageTvResponse>

    @GET("tv/top_rated")
    suspend fun getTopRatedTv(@Query("page") page: Int = 1): Response<HomePageTvResponse>

    @GET("tv/airing_today")
    suspend fun getTvAiringToday(@Query("page") page: Int = 1): Response<HomePageTvResponse>

    @GET("tv/on_the_air")
    suspend fun getTvOnTheAir(@Query("page") page: Int = 1): Response<HomePageTvResponse>

    @GET("tv/{id}")
    suspend fun getTvShowById(@Path("id") id: Int): Response<out DetailedTv>

    @GET("tv/{id}/images")
    suspend fun getTvImages(@Path("id") id: Int): Response<ImageCollection>

    @GET("tv/{id}/aggregate_credits")
    suspend fun getCastAndCrew(@Path("id") id: Int): Response<TvCastAndCrew>

    @GET("tv/{id}/content_ratings")
    suspend fun getContentRatings(@Path("id") id: Int): Response<TvContentRatings>

    @GET("tv/{id}/similar")
    suspend fun getSimilarTvShows(@Path("id") id: Int, @Query("page") page: Int = 1): Response<HomePageTvResponse>

    @GET("tv/{id}/videos")
    suspend fun getVideos(@Path("id") id: Int): Response<VideoResponse>

    @GET("tv/{id}/reviews")
    suspend fun getReviews(@Path("id") id: Int): Response<ReviewResponse>

    @GET("tv/{id}/keywords")
    suspend fun getKeywords(@Path("id") id: Int): Response<KeywordsResponse>

    @POST("tv/{id}/rating")
    suspend fun postTvRatingAsUser(
        @Path("id") id: Int,
        @Query("session_id") sessionId: String,
        @Body ratingBody: RatingBody
    ): Response<StatusResponse>

    @DELETE("tv/{id}/rating")
    suspend fun deleteTvReviewAsUser(
        @Path("id") id: Int,
        @Query("session_id") sessionId: String
    ): Response<StatusResponse>

    @GET("tv/{id}/season/{season}")
    suspend fun getSeason(@Path("id") seriesId: Int, @Path("season") seasonNumber: Int): Response<Season>

    @GET("tv/{id}/watch/providers")
    suspend fun getWatchProviders(@Path("id") seriesId: Int): Response<WatchProviderResponse>

    @GET("tv/{id}/external_ids")
    suspend fun getExternalIds(@Path("id") id: Int): Response<ExternalIds>

    @GET("tv/{id}/account_states")
    suspend fun getAccountStates(@Path("id") id: Int): Response<AccountStates>

    @GET("discover/tv")
    suspend fun discover(@Query("page") page: Int, @Query("with_keywords") keywords: String? = null): Response<SearchResult<SearchResultTv>>

    @GET("trending/tv/{time_window}")
    suspend fun trending(@Path("time_window") timeWindow: String, @Query("page") page: Int): Response<SearchResult<SearchResultTv>>
}