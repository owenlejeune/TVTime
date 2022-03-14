package com.owenlejeune.tvtime.api.tmdb.api.v4.model

import com.google.gson.annotations.SerializedName

class CreateListResponse(
    @SerializedName("status_message") val statusMessage: String,
    @SerializedName("id") val id: Int,
    @SerializedName("success") val isSuccess: Boolean,
    @SerializedName("status_code") val statusCode: Int
)