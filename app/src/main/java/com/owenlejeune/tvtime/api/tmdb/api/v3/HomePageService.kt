package com.owenlejeune.tvtime.api.tmdb.api.v3

import com.owenlejeune.tvtime.api.tmdb.api.v3.model.HomePageResponse
import retrofit2.Response

interface HomePageService {

    suspend fun getNowPlaying(page: Int = 1): Response<out HomePageResponse>

    suspend fun getPopular(page: Int = 1): Response<out HomePageResponse>

    suspend fun getTopRated(page: Int = 1): Response<out HomePageResponse>

    suspend fun getUpcoming(page: Int = 1): Response<out HomePageResponse>

}