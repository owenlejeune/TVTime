package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

data class PopularTv(
    @SerializedName("poster_path") override val posterPath: String?,
    @SerializedName("name") val name: String,
    @SerializedName("id") override val id: Int
): MediaItem(posterPath, name, id)