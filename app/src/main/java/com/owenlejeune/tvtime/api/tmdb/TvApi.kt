package com.owenlejeune.tvtime.api.tmdb

import com.owenlejeune.tvtime.api.tmdb.model.*
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

    @GET("tv/{id}/credits")
    suspend fun getCastAndCrew(@Path("id") id: Int): Response<CastAndCrew>

    @GET("tv/{id}/content_ratings")
    suspend fun getContentRatings(@Path("id") id: Int): Response<TvContentRatings>

    @GET("tv/{id}/similar")
    suspend fun getSimilarTvShows(@Path("id") id: Int, @Query("page") page: Int = 1): Response<HomePageTvResponse>

    @GET("tv/{id}/videos")
    suspend fun getVideos(@Path("id") id: Int): Response<VideoResponse>

    @GET("tv/{id}/reviews")
    suspend fun getReviews(@Path("id") id: Int): Response<ReviewResponse>

    @POST("tv/{id}/rating")
    suspend fun postTvRatingAsGuest(
        @Path("id") id: Int,
        @Query("guest_session_id") guestSessionId: String,
        @Body ratingBody: RatingBody
    ): Response<RatingResponse>

    @POST("tv/{id}/rating")
    suspend fun postTvRatingAsUser(
        @Path("id") id: Int,
        @Query("session_id") sessionId: String,
        @Body ratingBody: RatingBody
    ): Response<RatingResponse>

    @DELETE("tv/{id}/rating")
    suspend fun deleteTvReviewAsGuest(
        @Path("id") id: Int,
        @Query("guest_session_id") guestSessionId: String
    ): Response<RatingResponse>

    @DELETE("tv/{id}/rating")
    suspend fun deleteTvReviewAsUser(
        @Path("id") id: Int,
        @Query("session_id") sessionId: String
    ): Response<RatingResponse>

}