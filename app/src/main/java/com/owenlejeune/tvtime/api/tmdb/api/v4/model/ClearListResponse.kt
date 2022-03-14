package com.owenlejeune.tvtime.api.tmdb.api.v4.model

import com.google.gson.annotations.SerializedName

class ClearListResponse(
    @SerializedName("items_deleted") val deletedItemsCount: Int,
    @SerializedName("status_message") val statusMessage: String,
    @SerializedName("id") val id: Int,
    @SerializedName("status_code") val statusCode: Int,
    @SerializedName("success") val isSuccess: Boolean
)
