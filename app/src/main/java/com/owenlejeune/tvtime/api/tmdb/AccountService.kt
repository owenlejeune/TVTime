package com.owenlejeune.tvtime.api.tmdb

import com.owenlejeune.tvtime.api.tmdb.model.*
import retrofit2.Response

class AccountService {

    private val accountService by lazy { TmdbClient().createAccountService() }

    suspend fun getAccountDetails(): Response<AccountDetails> {
        return accountService.getAccountDetails()
    }

    suspend fun getFavoriteMovies(accountId: Int, page: Int = 1): Response<FavoriteMediaResponse<FavoriteMovie>> {
        return accountService.getFavoriteMovies(accountId, page)
    }

    suspend fun getFavoriteTvShows(accountId: Int, page: Int = 1): Response<FavoriteMediaResponse<FavoriteTvSeries>> {
        return accountService.getFavoriteTvShows(accountId, page)
    }

    suspend fun markAsFavorite(accountId: Int, body: MarkAsFavoriteBody): Response<StatusResponse> {
        return accountService.markAsFavorite(accountId, body)
    }

    suspend fun getRatedMovies(accountId: Int, page: Int = 1): Response<RatedMediaResponse<RatedMovie>> {
        return accountService.getRatedMovies(accountId, page)
    }

    suspend fun getRatedTvShows(accountId: Int, page: Int = 1): Response<RatedMediaResponse<RatedTv>> {
        return accountService.getRatedTvShows(accountId, page)
    }

    suspend fun getRatedTvEpisodes(accountId: Int, page: Int = 1): Response<RatedMediaResponse<RatedEpisode>> {
        return accountService.getRatedTvEpisodes(accountId, page)
    }

    suspend fun getMovieWatchlist(accountId: Int, page: Int = 1): Response<WatchlistResponse<WatchlistMovie>> {
        return accountService.getMovieWatchlist(accountId, page)
    }

    suspend fun getTvWatchlist(accountId: Int, page: Int = 1): Response<WatchlistResponse<WatchlistTvSeries>> {
        return accountService.getTvWatchlist(accountId, page)
    }

    suspend fun addToWatchlist(accountId: Int, body: WatchlistBody): Response<StatusResponse> {
        return accountService.addToWatchlist(accountId, body)
    }

}