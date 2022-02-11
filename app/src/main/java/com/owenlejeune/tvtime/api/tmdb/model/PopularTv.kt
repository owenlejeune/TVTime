package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

data class PopularTv(
    @SerializedName("id") override val id: Int,
    @SerializedName("name") override val title: String,
    @SerializedName("poster_path") override val posterPath: String?
): TmdbItem(id, title, posterPath)