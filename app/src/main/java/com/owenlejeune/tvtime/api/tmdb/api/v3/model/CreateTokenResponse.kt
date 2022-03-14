package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName

class CreateTokenResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("request_token") val requestToken: String,
    @SerializedName("expires_at") val expiry: String
)
