package com.owenlejeune.tvtime.api.tmdb.api.v3

import android.util.Log
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.AccountDetails
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.MarkAsFavoriteBody
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.WatchlistBody
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

}