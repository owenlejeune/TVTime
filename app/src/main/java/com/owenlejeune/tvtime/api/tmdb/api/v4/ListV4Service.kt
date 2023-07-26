package com.owenlejeune.tvtime.api.tmdb.api.v4

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateMapOf
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.AddToListBody
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
    private val context: Context by inject()

    val listMap = mutableStateMapOf<Int, MediaList>()

    suspend fun getList(listId: Int) {
        val response = service.getList(listId)
        if (response.isSuccessful) {
            response.body()?.let {
                listMap[listId] = it
            }
        }
    }

    suspend fun createList(
        name: String,
        language: String,
        description: String,
        isPublic: Boolean,
        localeCode: String
    ) {
        service.createList(name, language, description, isPublic, localeCode)
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

    suspend fun addItemsToList(listId: Int, body: AddToListBody) {
        val response = service.addItemsToList(listId, body)
        if (response.isSuccessful) {
            response.body()?.let {
                if (it.isSuccess) {
                    Toast.makeText(context, "Successfully added to list", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Error: This item already exists in list", Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show()
        }
    }

    suspend fun updateListItems(listId: Int, body: UpdateListItemBody) {//}: Response<AddToListResponse> {
        service.updateListItems(listId, body)
    }

    suspend fun getListItemStatus(listId: Int, mediaId: Int, mediaType: String) {//}: Response<ListItemStatusResponse> {
        service.getListItemStatus(listId, mediaId, mediaType)
    }
}