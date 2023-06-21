package com.owenlejeune.tvtime.api.tmdb.api.v3

import com.owenlejeune.tvtime.api.tmdb.api.v3.model.*
import retrofit2.Response
import retrofit2.http.*

interface AccountApi {

    @GET("account")
    suspend fun getAccountDetails(): Response<AccountDetails>

    @POST("account/{id}/favorite")
    suspend fun markAsFavorite(
        @Path("id") id: Int,
        @Body body: MarkAsFavoriteBody
    ): Response<StatusResponse>

    @POST("account/{id}/watchlist")
    suspend fun addToWatchlist(
        @Path("id") id: Int,
        @Body body: WatchlistBody
    ): Response<StatusResponse>

}