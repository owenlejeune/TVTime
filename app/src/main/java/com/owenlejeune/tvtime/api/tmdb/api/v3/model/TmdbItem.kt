package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName

abstract class TmdbItem(
    @SerializedName("id") val id: Int,
    @SerializedName("poster_path", alternate = ["profile_path"]) val posterPath: String?,
    @SerializedName("name", alternate = ["title"]) val title: String
)