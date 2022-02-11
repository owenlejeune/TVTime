package com.owenlejeune.tvtime.api.tmdb.model

import com.owenlejeune.tvtime.api.tmdb.model.DetailedItem

class DetailedTv(
    id: Int,
    title: String,
    posterPath: String?
): DetailedItem(id, title, posterPath)