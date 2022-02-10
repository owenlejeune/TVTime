package com.owenlejeune.tvtime.api.tmdb

import com.owenlejeune.tvtime.api.tmdb.model.PopularTvResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TvApi {

    @GET("tv/popular")
    fun getPoplarTv(@Query("page") page: Int = 1): Call<PopularTvResponse>

}