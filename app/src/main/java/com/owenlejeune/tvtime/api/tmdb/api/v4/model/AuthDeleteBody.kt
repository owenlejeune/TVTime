package com.owenlejeune.tvtime.api.tmdb.api.v4.model

import com.google.gson.annotations.SerializedName

class AuthDeleteBody(
    @SerializedName("access_token") val accessToken: String
)
