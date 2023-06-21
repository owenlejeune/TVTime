package com.owenlejeune.tvtime.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.owenlejeune.tvtime.api.tmdb.api.v3.AccountService
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.MarkAsFavoriteBody
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.WatchlistBody
import com.owenlejeune.tvtime.api.tmdb.api.v4.ListV4Service
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.DeleteListItemsBody
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.DeleteListItemsItem
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.ListUpdateBody
import com.owenlejeune.tvtime.utils.SessionManager
import com.owenlejeune.tvtime.utils.types.MediaViewType
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AccountViewModel: ViewModel(), KoinComponent {

    private val listService: ListV4Service by inject()
    private val accountService: AccountService by inject()

    val listMap = listService.listMap

    suspend fun getList(listId: Int) {
        listService.getList(listId = listId)
    }

    suspend fun deleteListItem(listId: Int, itemId: Int, itemType: MediaViewType) {
        val removeItemBody = DeleteListItemsItem(itemId, itemType)
        listService.deleteListItems(listId, DeleteListItemsBody(listOf(removeItemBody)))
    }

    suspend fun updateList(listId: Int, body: ListUpdateBody) {
        listService.updateList(listId, body)
    }

    suspend fun addToFavourites(type: MediaViewType, itemId: Int, favourited: Boolean) {
        val accountId = SessionManager.currentSession.value?.accountDetails?.value?.id ?: throw Exception("Session must not be null")
        accountService.markAsFavorite(accountId, MarkAsFavoriteBody(type, itemId, favourited))
    }

    suspend fun addToWatchlist(type: MediaViewType, itemId: Int, watchlisted: Boolean) {
        val accountId = SessionManager.currentSession.value?.accountDetails?.value?.id ?: throw Exception("Session must not be null")
        accountService.addToWatchlist(accountId, WatchlistBody(type, itemId, watchlisted))
    }

}