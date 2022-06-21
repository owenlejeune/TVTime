package com.owenlejeune.tvtime.api.tmdb.api.v4

import com.owenlejeune.tvtime.api.tmdb.TmdbClient
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.*
import retrofit2.Response

class ListV4Service {

    private val service by lazy { TmdbClient().createV4ListService() }

    suspend fun getLists(listId: Int, apiKey: String, page: Int = 1): Response<MediaList> {
        return service.getList(listId, apiKey, page)
    }

    suspend fun createList(body: CreateListBody): Response<CreateListResponse> {
        return service.createList(body)
    }

    suspend fun updateList(listId: Int, body: ListUpdateBody): Response<StatusResponse> {
        return service.updateList(listId, body)
    }

    suspend fun clearList(listId: Int): Response<ClearListResponse> {
        return service.clearList(listId)
    }

    suspend fun deleteList(listId: Int): Response<StatusResponse> {
        return service.deleteList(listId)
    }

    suspend fun addItemsToList(listId: Int, body: AddToListBody): Response<AddToListResponse> {
        return service.addItemsToList(listId, body)
    }

    suspend fun updateListItems(listId: Int, body: UpdateListItemBody): Response<AddToListResponse> {
        return service.updateListItems(listId, body)
    }

    suspend fun deleteListItems(listId: Int, body: DeleteListItemsBody): Response<AddToListResponse> {
        return service.deleteListItems(listId, body)
    }

    suspend fun getListItemStatus(listId: Int, mediaId: Int, mediaType: String): Response<ListItemStatusResponse> {
        return service.getListItemStatus(listId, mediaId, mediaType)
    }
}