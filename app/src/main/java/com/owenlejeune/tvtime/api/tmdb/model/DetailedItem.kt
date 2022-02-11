package com.owenlejeune.tvtime.api.tmdb.model

abstract class DetailedItem(id: Int, title: String, posterPath: String?): TmdbItem(id, title, posterPath)