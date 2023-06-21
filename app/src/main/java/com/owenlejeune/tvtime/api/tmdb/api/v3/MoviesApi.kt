package com.owenlejeune.tvtime.api.tmdb.api.v3

import com.owenlejeune.tvtime.api.tmdb.api.v3.model.*
import retrofit2.Response
import retrofit2.http.*

interface MoviesApi {

    @GET("movie/popular")
    suspend fun getPopularMovies(@Query("page") page: Int = 1): Response<HomePageMoviesResponse>

    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(@Query("page") page: Int = 1): Response<HomePageMoviesResponse>

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(@Query("page") page: Int = 1): Response<HomePageMoviesResponse>

    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(@Query("page") page: Int = 1): Response<HomePageMoviesResponse>

    @GET("movie/{id}")
    suspend fun getMovieById(@Path("id") id: Int): Response<DetailedMovie>

    @GET("movie/{id}/images")
    suspend fun getMovieImages(@Path("id") id: Int): Response<ImageCollection>

    @GET("movie/{id}/credits")
    suspend fun getCastAndCrew(@Path("id") id: Int): Response<CastAndCrew>

    @GET("movie/{id}/release_dates")
    suspend fun getReleaseDates(@Path("id") id: Int): Response<MovieReleaseResults>

    @GET("movie/{id}/recommendations")
    suspend fun getSimilarMovies(@Path("id") id: Int, @Query("page") page: Int = 1): Response<HomePageMoviesResponse>

    @GET("movie/{id}/videos")
    suspend fun getVideos(@Path("id") id: Int): Response<VideoResponse>

    @GET("movie/{id}/reviews")
    suspend fun getReviews(@Path("id") id: Int): Response<ReviewResponse>

    @GET("movie/{id}/keywords")
    suspend fun getKeywords(@Path("id") id: Int): Response<KeywordsResponse>

    @POST("movie/{id}/rating")
    suspend fun postMovieRatingAsUser(
        @Path("id") id: Int,
        @Query("session_id") sessionId: String,
        @Body ratingBody: RatingBody
    ): Response<StatusResponse>

    @DELETE("movie/{id}/rating")
    suspend fun deleteMovieReviewAsUser(
        @Path("id") id: Int,
        @Query("session_id") sessionId: String
    ): Response<StatusResponse>

    @GET("movie/{id}/watch/providers")
    suspend fun getWatchProviders(@Path("id") id: Int): Response<WatchProviderResponse>

    @GET("movie/{id}/external_ids")
    suspend fun getExternalIds(@Path("id") id: Int): Response<ExternalIds>

    @GET("movie/{id}/account_states")
    suspend fun getAccountStates(@Path("id") id: Int, @Query("session_id") sessionId: String): Response<AccountStates>

}