package com.owenlejeune.tvtime.api.tmdb

import com.owenlejeune.tvtime.api.tmdb.model.MediaItem

object TmdbUtils {

    fun getFullPosterPath(posterPath: String?): String {
        return "https://image.tmdb.org/t/p/original${posterPath}"
    }

    fun getFullPosterPath(mediaItem: MediaItem): String {
        return getFullPosterPath(mediaItem.posterPath)
    }

}