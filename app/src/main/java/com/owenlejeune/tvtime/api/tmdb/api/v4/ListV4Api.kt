package com.owenlejeune.tvtime.api.tmdb.api.v4

import com.owenlejeune.tvtime.api.tmdb.api.v4.model.*
import retrofit2.Response
import retrofit2.http.*

interface ListV4Api {

    @GET("list/{id}")
    suspend fun getList(
        @Path("id") listId: Int,
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): Response<MediaList>

    @POST("list")
    suspend fun createList(body: CreateListBody): Response<CreateListResponse>

    @PUT("list/{id}")
    suspend fun updateList(@Path("id") listId: Int, body: ListUpdateBody): Response<StatusResponse>

    @GET("list/{id}/clear")
    suspend fun clearList(@Path("id") listId: Int): Response<ClearListResponse>

    @DELETE("list/{id}")
    suspend fun deleteList(@Path("id") listId: Int): Response<StatusResponse>

    @POST("list/{id}/items")
    suspend fun addItemsToList(@Path("id") listId: Int, body: AddToListBody): Response<AddToListResponse>

    @PUT("list/{id}/items")
    suspend fun updateListItems(@Path("id") listId: Int, body: UpdateListItemBody): Response<AddToListResponse>

    @DELETE("list/{id}/items")
    suspend fun deleteListItems(@Path("id") listId: Int, body: DeleteListItemsBody): Response<AddToListResponse>

    @GET("list/{id}/item_status")
    suspend fun getListItemStatus(@Path("id") listId: Int, @Query("media_id") mediaId: Int, @Query("media_type") mediaType: String): Response<ListItemStatusResponse>

}