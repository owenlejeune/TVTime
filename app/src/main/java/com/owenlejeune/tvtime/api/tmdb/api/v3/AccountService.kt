package com.owenlejeune.tvtime.api.tmdb.api.v3

import android.util.Log
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.AccountDetails
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.FavoriteMediaResponse
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.FavoriteMovie
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.FavoriteTvSeries
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.MarkAsFavoriteBody
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.RatedEpisode
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.RatedMediaResponse
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.RatedMovie
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.RatedTv
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.WatchlistBody
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.WatchlistMovie
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.WatchlistResponse
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.WatchlistTvSeries
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.Response

class AccountService: KoinComponent {

    private val TAG = "AccountService"

    private val accountService: AccountApi by inject()

    suspend fun getAccountDetails(): Response<AccountDetails> {
        return accountService.getAccountDetails()
    }

    suspend fun markAsFavorite(accountId: Int, body: MarkAsFavoriteBody) {
        val response = accountService.markAsFavorite(accountId, body)
        if (response.isSuccessful) {
            Log.d(TAG, "Successfully marked as favourite")
        } else {
            Log.e(TAG, "Issue marking as favourite")
        }
    }

    suspend fun addToWatchlist(accountId: Int, body: WatchlistBody) {
        val response = accountService.addToWatchlist(accountId, body)
        if (response.isSuccessful) {
            Log.d(TAG, "Successfully added to watchlist")
        } else {
            Log.e(TAG, "Issue adding to watchlist")
        }
    }

    // TODO - replace these with account states API calls
    suspend fun getFavoriteMovies(accountId: Int, page: Int = 1): Response<FavoriteMediaResponse<FavoriteMovie>> {
        return accountService.getFavoriteMovies(accountId, page)
    }

    suspend fun getFavoriteTvShows(accountId: Int, page: Int = 1): Response<FavoriteMediaResponse<FavoriteTvSeries>> {
        return accountService.getFavoriteTvShows(accountId, page)
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

}