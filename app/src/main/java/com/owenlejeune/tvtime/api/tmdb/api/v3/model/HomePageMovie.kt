package com.owenlejeune.tvtime.api.tmdb.api.v3.model

class HomePageMovie(
    id: Int,
    posterPath: String?,
    title: String
): TmdbItem(id, posterPath, title)