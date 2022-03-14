package com.owenlejeune.tvtime.api.tmdb.api.v3

import com.owenlejeune.tvtime.api.tmdb.api.v3.model.*
import retrofit2.Response
import retrofit2.http.*

interface AccountApi {

    @GET("account")
    suspend fun getAccountDetails(): Response<AccountDetails>

    @GET("account/{id}/lists")
    suspend fun getLists(
        @Path("id") id: Int,
        @Query("page") page: Int
    ): Response<AccountListResponse>

    @GET("account/{id}/favorite/movies")
    suspend fun getFavoriteMovies(
        @Path("id") id: Int,
        @Query("page") page: Int
    ): Response<FavoriteMediaResponse<FavoriteMovie>>

    @GET("account/{id}/favorite/tv")
    suspend fun getFavoriteTvShows(
        @Path("id") id: Int,
        @Query("page") page: Int
    ): Response<FavoriteMediaResponse<FavoriteTvSeries>>

//    @Headers("Content-Type: application/json;charset=utf-8")
    @POST("account/{id}/favorite")
    suspend fun markAsFavorite(
        @Path("id") id: Int,
        @Body body: MarkAsFavoriteBody
    ): Response<StatusResponse>

    @GET("account/{id}/rated/movies")
    suspend fun getRatedMovies(
        @Path("id") id: Int,
        @Query("page") page: Int
    ): Response<RatedMediaResponse<RatedMovie>>

    @GET("account/{id}/rated/tv")
    suspend fun getRatedTvShows(
        @Path("id") id: Int,
        @Query("page") page: Int
    ): Response<RatedMediaResponse<RatedTv>>

    @GET("account/{id}/rated/tv/episodes")
    suspend fun getRatedTvEpisodes(
        @Path("id") id: Int,
        @Query("page") page: Int
    ): Response<RatedMediaResponse<RatedEpisode>>

    @GET("account/{id}/watchlist/movies")
    suspend fun getMovieWatchlist(
        @Path("id") id: Int,
        @Query("page") page: Int
    ): Response<WatchlistResponse<WatchlistMovie>>

    @GET("account/{id}/watchlist/tv")
    suspend fun getTvWatchlist(
        @Path("id") id: Int,
        @Query("page") page: Int
    ): Response<WatchlistResponse<WatchlistTvSeries>>

//    @Headers("Content-Type: application/json;charset=utf-8")
    @POST("account/{id}/watchlist")
    suspend fun addToWatchlist(
        @Path("id") id: Int,
        @Body body: WatchlistBody
    ): Response<StatusResponse>

}