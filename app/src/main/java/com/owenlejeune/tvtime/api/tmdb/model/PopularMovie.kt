package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

class PopularMovie(
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("title") val title: String
)