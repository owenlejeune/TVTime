package com.owenlejeune.tvtime.utils

import androidx.compose.ui.text.intl.Locale
import com.owenlejeune.tvtime.api.tmdb.model.*

object TmdbUtils {

    private const val POSTER_BASE = "https://image.tmdb.org/t/p/original"
    private const val BACKDROP_BASE = "https://www.themoviedb.org/t/p/original"
    private const val PERSON_BASE = "https://www.themoviedb.org/t/p/w600_and_h900_bestv2"

    private const val DEF_REGION = "US"

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

    fun getMovieReleaseYear(movie: DetailedMovie): String {
        return movie.releaseDate.split("-")[0]
    }

    fun getSeriesRun(series: DetailedTv): String {
        val startYear = getTvStartYear(series)
        val endYear = if (series.status == "Active") {
            getTvEndYear(series)
        } else {
            ""
        }
        return "${startYear}-${endYear}"
    }

    fun getTvStartYear(series: DetailedTv): String {
        return series.firstAirDate.split("-")[0]
    }

    fun getTvEndYear(series: DetailedTv): String {
        return series.lastAirDate.split("-")[0]
    }

    fun convertRuntimeToHoursMinutes(movie: DetailedMovie): String {
        movie.runtime?.let { runtime ->
            return convertRuntimeToHoursAndMinutes(runtime)
        }
        return ""
    }

    fun convertRuntimeToHoursMinutes(series: DetailedTv): String {
        return convertRuntimeToHoursAndMinutes(series.episodeRuntime[0])
    }

    private fun convertRuntimeToHoursAndMinutes(runtime: Int): String {
        val hours = runtime / 60
        val minutes = runtime % 60
        return if (hours > 0){
            if (minutes > 0) {
                "${hours}h${minutes}"
            } else {
                "${hours}h"
            }
        } else {
            "${minutes}m"
        }
    }

    fun getMovieRating(releases: MovieReleaseResults?): String {
        if (releases == null) {
            return ""
        }

        val currentRegion = Locale.current.language
        val certifications = HashMap<String, String>()
        releases.releaseDates.forEach { releaseDateResult ->
            if (releaseDateResult.region == currentRegion || releaseDateResult.region == DEF_REGION) {
                val cert = releaseDateResult.releaseDates.firstOrNull { it.certification.isNotEmpty() }
                if (cert != null) {
                    certifications[releaseDateResult.region] = cert.certification
                }
            }
        }
        if (certifications.isNotEmpty()) {
            return certifications[currentRegion] ?: certifications[DEF_REGION] ?: ""
        }
        return ""
    }

    fun getTvRating(contentRatings: TvContentRatings?): String {
        if (contentRatings == null) {
            return ""
        }

        val currentRegion = Locale.current.language
        val certifications = HashMap<String, String>()
        contentRatings.results.forEach { contentRating ->
            if (contentRating.language == currentRegion || contentRating.language == DEF_REGION) {
                certifications[contentRating.language] = contentRating.rating
            }
        }
        if (certifications.isNotEmpty()) {
            return certifications[currentRegion] ?: certifications[DEF_REGION] ?: ""
        }
        return ""
    }

    fun convertVoteAverageToPercentage(detailItem: DetailedItem): Float {
        return detailItem.voteAverage / 10f
    }

}