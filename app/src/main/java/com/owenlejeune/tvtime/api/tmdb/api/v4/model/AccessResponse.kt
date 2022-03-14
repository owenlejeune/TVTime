package com.owenlejeune.tvtime.api.tmdb.api.v4.model

import com.google.gson.annotations.SerializedName

class AccessResponse(
    @SerializedName("status_message") val statusMessage: String,
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("success") val success: Boolean,
    @SerializedName("status_code") val statusCode: Int,
    @SerializedName("account_id") val accountId: String
)
