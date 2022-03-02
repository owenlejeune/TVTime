package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

class HomePageMovie(
    id: Int,
    posterPath: String?,
    title: String
): TmdbItem(id, posterPath, title)