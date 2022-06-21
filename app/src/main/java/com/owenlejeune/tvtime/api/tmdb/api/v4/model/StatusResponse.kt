package com.owenlejeune.tvtime.api.tmdb.api.v4.model

import com.google.gson.annotations.SerializedName

open class StatusResponse(
    @SerializedName("status_message") val statusMessage: String,
    @SerializedName("success") val isSuccess: Boolean,
    @SerializedName("status_code") val statusCode: Int
)
