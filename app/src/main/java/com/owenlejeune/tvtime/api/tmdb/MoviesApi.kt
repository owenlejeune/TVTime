package com.owenlejeune.tvtime.api.tmdb

import com.owenlejeune.tvtime.api.tmdb.model.PopularMoviesResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MoviesApi {

    @GET("movie/popular")
    fun getPopularMovies(@Query("page") page: Int = 1): Call<PopularMoviesResponse>
//    suspend fun getPopularMovies(@Query("page") page: Int = 1): PopularMoviesResponse

}