package com.owenlejeune.tvtime.api.tmdb

import com.owenlejeune.tvtime.api.tmdb.model.ImageCollection
import com.owenlejeune.tvtime.api.tmdb.model.PopularTvResponse
import com.owenlejeune.tvtime.api.tmdb.model.DetailedTv
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TvApi {

    @GET("tv/popular")
    suspend fun getPoplarTv(@Query("page") page: Int = 1): Response<PopularTvResponse>

    @GET("tv/{id}")
    suspend fun getTvShowById(@Path("id") id: Int): Response<out DetailedTv>

    @GET("tv/{id}/images")
    suspend fun getTvImages(@Path("id") id: Int): Response<ImageCollection>

}