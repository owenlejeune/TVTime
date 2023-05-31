package com.owenlejeune.tvtime.api.tmdb.api.v4

import com.owenlejeune.tvtime.api.tmdb.TmdbClient
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.FavoriteMovie
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.FavoriteTvSeries
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.RecommendedMovie
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.RecommendedTv
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.V4AccountList
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.V4AccountResponse
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.V4RatedMovie
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.V4RatedTv
import retrofit2.Response

class AccountV4Service {

    private val service by lazy { TmdbClient().createV4AccountService() }

    suspend fun getLists(accountId: String, page: Int = 1): Response<V4AccountResponse<V4AccountList>> {
        return service.getLists(accountId, page)
    }

    suspend fun getFavoriteMovies(accountId: String, page: Int = 1): Response<V4AccountResponse<FavoriteMovie>> {
        return service.getFavoriteMovies(accountId, page)
    }

    suspend fun getFavoriteTvShows(accountId: String, page: Int = 1): Response<V4AccountResponse<FavoriteTvSeries>> {
        return service.getFavoriteTvShows(accountId, page)
    }

    suspend fun getMovieRecommendations(accountId: String, page: Int = 1): Response<V4AccountResponse<FavoriteMovie>> {
        return service.getMovieRecommendations(accountId, page)
    }

    suspend fun getTvShowRecommendations(accountId: String, page: Int = 1): Response<V4AccountResponse<FavoriteTvSeries>> {
        return service.getTvShowRecommendations(accountId, page)
    }

    suspend fun getMovieWatchlist(accountId: String, page: Int = 1): Response<V4AccountResponse<FavoriteMovie>> {
        return service.getMovieWatchlist(accountId, page)
    }

    suspend fun getTvShowWatchlist(accountId: String, page: Int = 1): Response<V4AccountResponse<FavoriteTvSeries>> {
        return service.getTvShowWatchlist(accountId, page)
    }

    suspend fun getRatedMovies(accountId: String, page: Int = 1): Response<V4AccountResponse<V4RatedMovie>> {
        return service.getRatedMovies(accountId, page)
    }

    suspend fun getRatedTvShows(accountId: String, page: Int = 1): Response<V4AccountResponse<V4RatedTv>> {
        return service.getRatedTvShows(accountId, page)
    }

    suspend fun getRecommendedMovies(accountId: String, page: Int = 1): Response<V4AccountResponse<RecommendedMovie>> {
        return service.getRecommendedMovies(accountId, page)
    }

    suspend fun getRecommendedTvSeries(accountId: String, page: Int): Response<V4AccountResponse<RecommendedTv>> {
        return service.getRecommendedTvSeries(accountId, page)
    }

}