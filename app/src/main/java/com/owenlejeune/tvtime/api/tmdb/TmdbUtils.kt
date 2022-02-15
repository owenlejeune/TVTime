package com.owenlejeune.tvtime.api.tmdb

import com.owenlejeune.tvtime.api.tmdb.model.DetailedItem
import com.owenlejeune.tvtime.api.tmdb.model.TmdbItem

object TmdbUtils {

    fun getFullPosterPath(posterPath: String?): String? {
        return posterPath?.let { "https://image.tmdb.org/t/p/original${posterPath}" }
    }

    fun getFullPosterPath(tmdbItem: TmdbItem?): String? {
        return tmdbItem?.let { getFullPosterPath(tmdbItem.posterPath) }
    }

    fun getFullBackdropPath(backdropPath: String?): String? {
        return backdropPath?.let { "https://www.themoviedb.org/t/p/original${backdropPath}" }
    }

    fun getFullBackdropPath(detailItem: DetailedItem?): String? {
        return detailItem?.let { getFullBackdropPath(detailItem.backdropPath) }
    }

}