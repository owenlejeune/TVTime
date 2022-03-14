package com.owenlejeune.tvtime.api.tmdb.api.v4.model

import com.google.gson.annotations.SerializedName

class AuthRequestBody(
    @SerializedName("redirect_to") val redirect: String
)
