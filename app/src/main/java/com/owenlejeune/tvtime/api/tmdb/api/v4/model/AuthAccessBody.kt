package com.owenlejeune.tvtime.api.tmdb.api.v4.model

import com.google.gson.annotations.SerializedName

class AuthAccessBody(
    @SerializedName("request_token") val requestToken: String
)
