package com.owenlejeune.tvtime.api.tmdb

import com.owenlejeune.tvtime.api.tmdb.model.DetailedItem
import com.owenlejeune.tvtime.api.tmdb.model.Image
import com.owenlejeune.tvtime.api.tmdb.model.Person
import com.owenlejeune.tvtime.api.tmdb.model.TmdbItem

object TmdbUtils {

    private const val POSTER_BASE = "https://image.tmdb.org/t/p/original"
    private const val BACKDROP_BASE = "https://www.themoviedb.org/t/p/original"
    private const val PERSON_BASE = "https://www.themoviedb.org/t/p/w600_and_h900_bestv2"

    fun getFullPosterPath(posterPath: String?): String? {
        return posterPath?.let { "https://image.tmdb.org/t/p/original${posterPath}" }
    }

    fun getFullPosterPath(tmdbItem: TmdbItem?): String? {
        return tmdbItem?.let { getFullPosterPath(tmdbItem.posterPath) }
    }

    fun getFullPosterPath(image: Image): String? {
        return getFullPosterPath(image.filePath)
    }

    fun getFullBackdropPath(backdropPath: String?): String? {
        return backdropPath?.let { "https://www.themoviedb.org/t/p/original${backdropPath}" }
    }

    fun getFullBackdropPath(detailItem: DetailedItem?): String? {
        return detailItem?.let { getFullBackdropPath(detailItem.backdropPath) }
    }

    fun getFullBackdropPath(image: Image): String? {
        return getFullBackdropPath(image.filePath)
    }

    fun getFullPersonImagePath(path: String?): String? {
        return path?.let { "https://www.themoviedb.org/t/p/w600_and_h900_bestv2${path}" }
    }

    fun getFullPersonImagePath(person: Person): String? {
        return getFullPersonImagePath(person.profilePath)
    }

}