package com.owenlejeune.tvtime.api.tmdb.api.v4

import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.AddToListBody
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.CreateListBody
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.DeleteListItemsBody
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.ListUpdateBody
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.MediaList
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.UpdateListItemBody
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ListV4Service: KoinComponent {

    companion object {
        private const val TAG = "ListV4Service"
    }

    private val service: ListV4Api by inject()

    val listMap = mutableStateMapOf<Int, MediaList>()

    suspend fun getList(listId: Int) {
        val response = service.getList(listId)
        if (response.isSuccessful) {
            response.body()?.let {
                listMap[listId] = it
            }
        }
    }

    suspend fun createList(body: CreateListBody) {//}: Response<CreateListResponse> {
        service.createList(body)
    }

    suspend fun updateList(listId: Int, body: ListUpdateBody) {
        val response = service.updateList(listId, body)
        if (response.isSuccessful) {
            Log.d(TAG, "Successfully updated list $listId")
            getList(listId)
        } else {
            Log.w(TAG, "Issue updating list $listId")
        }
    }

    suspend fun deleteListItems(listId: Int, body: DeleteListItemsBody) {
        val response = service.deleteListItems(listId, body)
        if (response.isSuccessful) {
            getList(listId)
        }
    }

    suspend fun clearList(listId: Int) {//}: Response<ClearListResponse> {
        service.clearList(listId)
    }

    suspend fun deleteList(listId: Int) {//}: Response<StatusResponse> {
        service.deleteList(listId)
    }

    suspend fun addItemsToList(listId: Int, body: AddToListBody) {//}: Response<AddToListResponse> {
        service.addItemsToList(listId, body)
    }

    suspend fun updateListItems(listId: Int, body: UpdateListItemBody) {//}: Response<AddToListResponse> {
        service.updateListItems(listId, body)
    }

    suspend fun getListItemStatus(listId: Int, mediaId: Int, mediaType: String) {//}: Response<ListItemStatusResponse> {
        service.getListItemStatus(listId, mediaId, mediaType)
    }
}