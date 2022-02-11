package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

class DetailedMovie(
    @SerializedName("id") override val id: Int,
    @SerializedName("origina_title") override val title: String,
    @SerializedName("poster_path") override val posterPath: String?
): DetailedItem(id, title, posterPath)
