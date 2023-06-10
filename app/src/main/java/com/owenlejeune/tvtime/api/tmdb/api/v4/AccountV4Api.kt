package com.owenlejeune.tvtime.api.tmdb.api.v4

import com.owenlejeune.tvtime.api.tmdb.api.v3.model.FavoriteMovie
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.FavoriteTvSeries
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.RecommendedMovie
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.RecommendedTv
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.AccountList
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.AccountResponse
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.V4RatedMovie
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.V4RatedTv
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AccountV4Api {

    @GET("account/{account_id}/lists")
    suspend fun getLists(@Path("account_id") accountId: String, @Query("page") page: Int = 1): Response<AccountResponse<AccountList>>

    @GET("account/{account_id}/movie/favorites")
    suspend fun getFavoriteMovies(@Path("account_id") accountId: String, @Query("page") page: Int = 1): Response<AccountResponse<FavoriteMovie>>

    @GET("account/{account_id}/tv/favorites")
    suspend fun getFavoriteTvShows(@Path("account_id") accountId: String, @Query("page") page: Int = 1): Response<AccountResponse<FavoriteTvSeries>>

    @GET("account/{account_id}/movie/recommendations")
    suspend fun getMovieRecommendations(@Path("account_id") accountId: String, @Query("page") page: Int = 1): Response<AccountResponse<FavoriteMovie>>

    @GET("account/{account_id}/tv/recommendations")
    suspend fun getTvShowRecommendations(@Path("account_id") accountId: String, @Query("page") page: Int = 1): Response<AccountResponse<FavoriteTvSeries>>

    @GET("account/{account_id}/movie/watchlist")
    suspend fun getMovieWatchlist(@Path("account_id") accountId: String, @Query("page") page: Int = 1): Response<AccountResponse<FavoriteMovie>>

    @GET("account/{account_id}/tv/watchlist")
    suspend fun getTvShowWatchlist(@Path("account_id") accountId: String, @Query("page") page: Int = 1): Response<AccountResponse<FavoriteTvSeries>>

    @GET("account/{account_id}/movie/rated")
    suspend fun getRatedMovies(@Path("account_id") accountId: String, @Query("page") page: Int = 1): Response<AccountResponse<V4RatedMovie>>

    @GET("account/{account_id}/tv/rated")
    suspend fun getRatedTvShows(@Path("account_id") accountId: String, @Query("page") page: Int = 1): Response<AccountResponse<V4RatedTv>>

    @GET("account/{account_id}/movie/recommendations")
    suspend fun getRecommendedMovies(@Path("account_id") accountId: String, @Query("page") page: Int = 1): Response<AccountResponse<RecommendedMovie>>

    @GET("account/{account_id}/tv/recommendations")
    suspend fun getRecommendedTvSeries(@Path("account_id") accountId: String, @Query("page") page: Int = 1): Response<AccountResponse<RecommendedTv>>
}