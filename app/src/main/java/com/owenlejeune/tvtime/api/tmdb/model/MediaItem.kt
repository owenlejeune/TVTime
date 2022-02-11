package com.owenlejeune.tvtime.api.tmdb.model

abstract class MediaItem(
    open val posterPath: String?,
    @Transient open val title: String,
    @Transient open val id: Int
)