package com.owenlejeune.tvtime.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.owenlejeune.tvtime.api.tmdb.api.createPagingFlow
import com.owenlejeune.tvtime.api.tmdb.api.v3.AccountService
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.MarkAsFavoriteBody
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.WatchlistBody
import com.owenlejeune.tvtime.api.tmdb.api.v4.AccountV4Service
import com.owenlejeune.tvtime.api.tmdb.api.v4.ListV4Service
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.AddToListBody
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.AddToListBodyItem
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.DeleteListItemsBody
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.DeleteListItemsItem
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.ListUpdateBody
import com.owenlejeune.tvtime.ui.screens.tabs.AccountTabNavItem
import com.owenlejeune.tvtime.utils.SessionManager
import com.owenlejeune.tvtime.utils.types.MediaViewType
import com.owenlejeune.tvtime.utils.types.ViewableMediaTypeException
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AccountViewModel: ViewModel(), KoinComponent {

    private val listService: ListV4Service by inject()
    private val accountService: AccountService by inject()
    private val accountV4Service: AccountV4Service by inject()

    private val accountId: String
        get() = SessionManager.currentSession.value?.accountId ?: ""

    val listMap = listService.listMap

    val ratedTv: Flow<PagingData<Any>> = createPagingFlow(
            fetcher = { p -> accountV4Service.getRatedTvShows(accountId, p) },
            processor = { it.results }
        )
    val favoriteTv: Flow<PagingData<Any>> = createPagingFlow(
            fetcher = { p -> accountV4Service.getFavoriteTvShows(accountId, p) },
            processor = { it.results }
        )
    val watchlistTv: Flow<PagingData<Any>> = createPagingFlow(
            fetcher = { p -> accountV4Service.getTvShowWatchlist(accountId, p) },
            processor = { it.results }
        )
    val recommendedTv: Flow<PagingData<Any>> = createPagingFlow(
            fetcher = { p -> accountV4Service.getRecommendedTvSeries(accountId, p) },
            processor = { it.results }
        )

    val ratedMovies: Flow<PagingData<Any>> = createPagingFlow(
            fetcher = { p -> accountV4Service.getRatedMovies(accountId, p) },
            processor = { it.results }
        )
    val favoriteMovies: Flow<PagingData<Any>> = createPagingFlow(
            fetcher = { p -> accountV4Service.getFavoriteMovies(accountId, p) },
            processor = { it.results }
        )
    val watchlistMovies: Flow<PagingData<Any>> = createPagingFlow(
            fetcher = { p -> accountV4Service.getMovieWatchlist(accountId, p) },
            processor = { it.results }
        )
    val recommendedMovies: Flow<PagingData<Any>> = createPagingFlow(
            fetcher = { p -> accountV4Service.getRecommendedMovies(accountId, p) },
            processor = { it.results }
        )

    val userLists: Flow<PagingData<Any>> = createPagingFlow(
            fetcher = { p -> accountV4Service.getLists(accountId, p) },
            processor = { it.results }
        )

    fun getPagingFlowFor(type: MediaViewType, accountTabType: AccountTabNavItem.AccountTabType): Flow<PagingData<Any>> {
        return when (accountTabType) {
            AccountTabNavItem.AccountTabType.LIST -> userLists
            AccountTabNavItem.AccountTabType.RATED -> {
                when (type) {
                    MediaViewType.MOVIE -> ratedMovies
                    MediaViewType.TV -> ratedTv
                    else -> throw ViewableMediaTypeException(type)
                }
            }
            AccountTabNavItem.AccountTabType.FAVORITE -> {
                when (type) {
                    MediaViewType.MOVIE -> favoriteMovies
                    MediaViewType.TV -> favoriteTv
                    else -> throw ViewableMediaTypeException(type)
                }
            }
            AccountTabNavItem.AccountTabType.WATCHLIST -> {
                when (type) {
                    MediaViewType.MOVIE -> watchlistMovies
                    MediaViewType.TV -> watchlistTv
                    else -> throw ViewableMediaTypeException(type)
                }
            }
            AccountTabNavItem.AccountTabType.RECOMMENDED -> {
                when (type) {
                    MediaViewType.MOVIE -> recommendedMovies
                    MediaViewType.TV -> recommendedTv
                    else -> throw ViewableMediaTypeException(type)
                }
            }
        }
    }

    fun getRecommendedFor(type: MediaViewType): Flow<PagingData<Any>> {
        return when (type) {
            MediaViewType.MOVIE -> recommendedMovies
            MediaViewType.TV -> recommendedTv
            else -> throw ViewableMediaTypeException(type)
        }
    }

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

    suspend fun addToList(listId: Int, itemId: Int, itemType: MediaViewType) {
        val body = AddToListBody(listOf(AddToListBodyItem(itemType, itemId)))
        addToList(listId, body)
    }

    suspend fun addToList(listId: Int, body: AddToListBody) {
        listService.addItemsToList(listId, body)
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