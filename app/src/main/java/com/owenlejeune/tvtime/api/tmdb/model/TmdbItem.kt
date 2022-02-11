package com.owenlejeune.tvtime.api.tmdb.model

abstract class TmdbItem(
    @Transient open val id: Int,
    @Transient open val title: String,
    @Transient open val posterPath: String?
)