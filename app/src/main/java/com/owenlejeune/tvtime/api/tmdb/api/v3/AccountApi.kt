package com.owenlejeune.tvtime.api.tmdb.api.v3

import com.owenlejeune.tvtime.api.tmdb.api.v3.model.*
import com.owenlejeune.tvtime.utils.types.MediaViewType
import retrofit2.Response
import retrofit2.http.*

interface AccountApi {

    @GET("account")
    suspend fun getAccountDetails(): Response<AccountDetails>

    @FormUrlEncoded
    @POST("account/{id}/favorite")
    suspend fun markAsFavorite(
        @Path("id") id: Int,
        @Field("media_type") mediaType: MediaViewType,
        @Field("media_id") mediaId: Int,
        @Field("favorite") isFavorite: Boolean
    ): Response<StatusResponse>

    @FormUrlEncoded
    @POST("account/{id}/watchlist")
    suspend fun addToWatchlist(
        @Path("id") id: Int,
        @Field("media_type") mediaType: MediaViewType,
        @Field("media_id") mediaId: Int,
        @Field("watchlist") onWatchlist: Boolean
    ): Response<StatusResponse>

}