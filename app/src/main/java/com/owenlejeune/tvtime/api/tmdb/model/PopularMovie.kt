package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

data class PopularMovie(
    @SerializedName("id") override val id: Int,
    @SerializedName("title") override val title: String,
    @SerializedName("poster_path") override val posterPath: String?
): TmdbItem(id, title, posterPath)