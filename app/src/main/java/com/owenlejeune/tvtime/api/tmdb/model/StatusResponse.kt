package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

class StatusResponse(
    @SerializedName("status_code") val statusCode: Int,
    @SerializedName("status_message") val statusMessage: String
)