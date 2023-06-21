package com.owenlejeune.tvtime.api.tmdb.api.v4

import com.owenlejeune.tvtime.api.tmdb.TmdbClient
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.FavoriteMovie
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.FavoriteTvSeries
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.WatchlistMovie
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.WatchlistTvSeries
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.AccountList
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.AccountResponse
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.RatedMovie
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.RatedTv
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.RecommendedMovie
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.RecommendedTv
import retrofit2.Response

class AccountV4Service {

    private val service by lazy { TmdbClient().createV4AccountService() }

    suspend fun getLists(accountId: String, page: Int): Response<AccountResponse<AccountList>> {
        return service.getLists(accountId, page)
    }

    suspend fun getFavoriteMovies(accountId: String, page: Int): Response<AccountResponse<FavoriteMovie>> {
        return service.getFavoriteMovies(accountId, page)
    }

    suspend fun getFavoriteTvShows(accountId: String, page: Int): Response<AccountResponse<FavoriteTvSeries>> {
        return service.getFavoriteTvShows(accountId, page)
    }

    suspend fun getMovieWatchlist(accountId: String, page: Int): Response<AccountResponse<WatchlistMovie>> {
        return service.getMovieWatchlist(accountId, page)
    }

    suspend fun getTvShowWatchlist(accountId: String, page: Int): Response<AccountResponse<WatchlistTvSeries>> {
        return service.getTvShowWatchlist(accountId, page)
    }

    suspend fun getRatedMovies(accountId: String, page: Int): Response<AccountResponse<RatedMovie>> {
        return service.getRatedMovies(accountId, page)
    }

    suspend fun getRatedTvShows(accountId: String, page: Int): Response<AccountResponse<RatedTv>> {
        return service.getRatedTvShows(accountId, page)
    }

    suspend fun getRecommendedMovies(accountId: String, page: Int ): Response<AccountResponse<RecommendedMovie>> {
        return service.getRecommendedMovies(accountId, page)
    }

    suspend fun getRecommendedTvSeries(accountId: String, page: Int): Response<AccountResponse<RecommendedTv>> {
        return service.getRecommendedTvSeries(accountId, page)
    }

}