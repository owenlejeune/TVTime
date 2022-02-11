package com.owenlejeune.tvtime.api.tmdb

import com.owenlejeune.tvtime.api.tmdb.model.TmdbItem

object TmdbUtils {

    fun getFullPosterPath(posterPath: String?): String {
        return "https://image.tmdb.org/t/p/original${posterPath}"
    }

    fun getFullPosterPath(tmdbItem: TmdbItem): String {
        return getFullPosterPath(tmdbItem.posterPath)
    }

}