package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName

class TokenSessionBody(
    @SerializedName("request_token") val requestToken: String
)
