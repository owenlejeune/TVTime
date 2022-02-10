package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

data class PopularMovie(
    @SerializedName("poster_path") override val posterPath: String?,
    @SerializedName("title") override val title: String
): MediaItem(posterPath, title)