package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

class TokenValidationBody(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String,
    @SerializedName("request_token") val requestToken: String
)