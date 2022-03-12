package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

class TokenSessionBody(
    @SerializedName("request_token") val requestToken: String
)
