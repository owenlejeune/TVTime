package com.owenlejeune.tvtime.utils

import androidx.compose.ui.text.intl.Locale
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.*
import java.text.SimpleDateFormat

object TmdbUtils {

    private const val POSTER_BASE = "https://image.tmdb.org/t/p/original"
    private const val BACKDROP_BASE = "https://www.themoviedb.org/t/p/original"
    private const val PERSON_BASE = "https://www.themoviedb.org/t/p/w600_and_h900_bestv2"
    private const val GRAVATAR_BASE = "https://www.gravatar.com/avatar/"
    private const val AVATAR_BASE = "https://www.themoviedb.org/t/p/w150_and_h150_face"

    private const val DEF_REGION = "US"

    fun getFullPosterPath(posterPath: String?): String? {
        return posterPath?.let {
            if (posterPath.isEmpty()) null else "${POSTER_BASE}${posterPath}"
        }
    }

    fun getFullPosterPath(tmdbItem: TmdbItem?): String? {
        return tmdbItem?.let { getFullPosterPath(tmdbItem.posterPath) }
    }

    fun getFullPosterPath(image: Image): String? {
        return getFullPosterPath(image.filePath)
    }

    fun getFullBackdropPath(backdropPath: String?): String? {
        return backdropPath?.let {
            if (backdropPath.isEmpty()) null else "${BACKDROP_BASE}${backdropPath}"
        }
    }

    fun getFullBackdropPath(detailItem: DetailedItem?): String? {
        return detailItem?.let { getFullBackdropPath(detailItem.backdropPath) }
    }

    fun getFullBackdropPath(image: Image): String? {
        return getFullBackdropPath(image.filePath)
    }

    fun getFullPersonImagePath(path: String?): String? {
        return path?.let { "${PERSON_BASE}${path}" }
    }

    fun getFullPersonImagePath(person: Person): String? {
        return getFullPersonImagePath(person.profilePath)
    }

    fun getFullAvatarPath(path: String?): String? {
        return path?.let {
            if (path.contains("http")) {
                return path.substring(startIndex = 1)
            }
            "${AVATAR_BASE}${path}"
        }
    }

    fun getFullAvatarPath(author: AuthorDetails): String? {
        return getFullAvatarPath(author.avatarPath)
    }

    fun getMovieReleaseYear(movie: DetailedMovie): String {
        return movie.releaseDate.split("-")[0]
    }

    fun getSeriesRun(series: DetailedTv): String {
        val startYear = getTvStartYear(series)
        val endYear = if (series.status == Status.ACTIVE) {
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

    fun convertRuntimeToHoursAndMinutes(runtime: Int): String {
        val hours = runtime / 60
        val minutes = runtime % 60
        return if (hours > 0){
            if (minutes > 0) {
                "${hours}h ${minutes}m"
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

    fun getFullVideoUrl(video: Video): String {
        if (video.site == "YouTube") {
            return "http://www.youtube.com/watch?v=${video.key}"
        }
        return ""
    }

    fun formatDate(inDate: String): String {
        val orig = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.US)
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.US)

        val date = orig.parse(inDate)//.replace("Z", "+0000"))
        return formatter.format(date)
    }

    fun getAccountGravatarUrl(accountDetails: AccountDetails): String {
        val hash = accountDetails.avatar.gravatar?.hash
        return "${GRAVATAR_BASE}${hash}"
    }

    fun getAccountGravatarUrl(gravatarHash: String): String {
        return "${GRAVATAR_BASE}${gravatarHash}"
    }

    fun getAccountAvatarUrl(accountDetails: AccountDetails): String {
        val path = accountDetails.avatar.tmdb?.avatarPath
        return "${AVATAR_BASE}${path}"
    }

    fun releaseYearFromData(releaseDate: String): String {
        if (releaseDate.length >=4) {
            return releaseDate.split("-").first { it.length == 4 }
        }
        return ""
    }

    fun formatRevenue(revenue: Int): String {
        val decFormat = "%.1f"
        val thousands = revenue.toFloat() / 1000f
        if (thousands > 1000) {
            val millions = thousands / 1000f
            if (millions > 1000) {
                val billions = millions / 1000f
                val bs = decFormat.format(billions)
                return "$${bs}B"
            }
            val ms = decFormat.format(millions)
            return "$${ms}M"
        }
        return "$${thousands}"
    }

}