package com.owenlejeune.tvtime.api.tmdb

import com.owenlejeune.tvtime.api.tmdb.model.ImageCollection
import com.owenlejeune.tvtime.api.tmdb.model.DetailedMovie
import com.owenlejeune.tvtime.api.tmdb.model.PopularMoviesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MoviesApi {

    @GET("movie/popular")
    suspend fun getPopularMovies(@Query("page") page: Int = 1): Response<PopularMoviesResponse>

    @GET("movie/{id}")
    suspend fun getMovieById(@Path("id") id: Int): Response<DetailedMovie>

    @GET("movie/{id}/images")
    suspend fun getMovieImages(@Path("id") id: Int): Response<ImageCollection>

}