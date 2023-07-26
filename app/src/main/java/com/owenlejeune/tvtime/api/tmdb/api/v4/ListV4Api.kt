package com.owenlejeune.tvtime.api.tmdb.api.v4

import com.owenlejeune.tvtime.api.tmdb.api.v4.model.*
import retrofit2.Response
import retrofit2.http.*

interface ListV4Api {

    @GET("list/{id}")
    suspend fun getList(@Path("id") listId: Int): Response<MediaList>

    @FormUrlEncoded
    @POST("list")
    suspend fun createList(
        @Field("name") name: String,
        @Field("iso_639_1") language: String,
        @Field("description") description: String,
        @Field("public") isPublic: Boolean,
        @Field("iso_3166_1") localeCode: String
    ): Response<CreateListResponse>

    @PUT("list/{id}")
    suspend fun updateList(@Path("id") listId: Int, @Body body: ListUpdateBody): Response<StatusResponse>

    @GET("list/{id}/clear")
    suspend fun clearList(@Path("id") listId: Int): Response<ClearListResponse>

    @DELETE("list/{id}")
    suspend fun deleteList(@Path("id") listId: Int): Response<StatusResponse>

    @POST("list/{id}/items")
    suspend fun addItemsToList(@Path("id") listId: Int, @Body body: AddToListBody): Response<AddToListResponse>

    @PUT("list/{id}/items")
    suspend fun updateListItems(@Path("id") listId: Int, @Body body: UpdateListItemBody): Response<AddToListResponse>

    @HTTP(method = "DELETE", path = "list/{id}/items", hasBody = true)
    suspend fun deleteListItems(@Path("id") listId: Int, @Body body: DeleteListItemsBody): Response<AddToListResponse>

    @GET("list/{id}/item_status")
    suspend fun getListItemStatus(@Path("id") listId: Int, @Query("media_id") mediaId: Int, @Query("media_type") mediaType: String): Response<ListItemStatusResponse>

}