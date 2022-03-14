package com.owenlejeune.tvtime.api.tmdb.api.v4.model

import com.google.gson.annotations.SerializedName

class AuthResponse(
    @SerializedName("status_message") val statusMessage: String,
    @SerializedName("request_token") val requestToken: String,
    @SerializedName("success") val success: Boolean,
    @SerializedName("status_code") val statusCode: Int
)
