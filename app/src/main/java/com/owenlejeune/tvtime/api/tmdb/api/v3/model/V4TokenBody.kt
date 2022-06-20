package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName

class V4TokenBody(
    @SerializedName("access_token") val accessToken: String
)