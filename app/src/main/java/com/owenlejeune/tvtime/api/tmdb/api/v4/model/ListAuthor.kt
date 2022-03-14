package com.owenlejeune.tvtime.api.tmdb.api.v4.model

import com.google.gson.annotations.SerializedName

class ListAuthor(
    @SerializedName("gravatar_hash") val gravatarHash: String,
    @SerializedName("name") val name: String,
    @SerializedName("username") val username: String
)