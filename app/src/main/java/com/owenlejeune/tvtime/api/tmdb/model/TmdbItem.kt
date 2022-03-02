package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

abstract class TmdbItem(
    @SerializedName("id") val id: Int,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("name", alternate = ["title"]) val title: String
)