package com.owenlejeune.tvtime.utils

import android.nfc.FormatException
import androidx.compose.ui.text.intl.Locale
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.AccountDetails
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.AuthorDetails
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.DetailedItem
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.DetailedMovie
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.DetailedTv
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.Episode
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.Image
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.MovieReleaseResults
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.Person
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.Status
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.TmdbItem
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.TvContentRatings
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.Video
import com.owenlejeune.tvtime.ui.viewmodel.ConfigurationViewModel
import java.text.SimpleDateFormat
import java.util.Date

object TmdbUtils {

    private const val GRAVATAR_BASE = "https://www.gravatar.com/avatar/"

    private const val DEF_REGION = "US"

    private var IMAGE_BASE = ""
    private var BACKDROP_SIZE = "original"
    private var LOGO_SIZE = "original"
    private var POSTER_SIZE = "original"
    private var PROFILE_SIZE = "original"
    private var STILL_SIZE = "original"

    fun setup(configurationViewModel: ConfigurationViewModel) {
        IMAGE_BASE = configurationViewModel.detailsConfiguration.value.images.secureBaseUrl
    }

    fun fullLogoPath(sourcePath: String) = IMAGE_BASE.plus(LOGO_SIZE).plus(sourcePath)

    fun getFullPosterPath(posterPath: String?): String? {
        return posterPath?.let {
            if (posterPath.isEmpty()) null else IMAGE_BASE.plus(POSTER_SIZE).plus(posterPath)
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
            if (backdropPath.isEmpty()) null else IMAGE_BASE.plus(BACKDROP_SIZE).plus(backdropPath)
        }
    }

    fun getFullBackdropPath(detailItem: DetailedItem?): String? {
        return detailItem?.let { getFullBackdropPath(detailItem.backdropPath) }
    }

    fun getFullBackdropPath(image: Image): String? {
        return getFullBackdropPath(image.filePath)
    }

    fun getFullPersonImagePath(path: String?): String? {
        return path?.let { IMAGE_BASE.plus(PROFILE_SIZE).plus(it) }
    }

    fun getFullPersonImagePath(person: Person): String? {
        return getFullPersonImagePath(person.profilePath)
    }

    fun getFullAvatarPath(path: String?): String? {
        return path?.let {
            if (path.contains("http")) {
                return path.substring(startIndex = 1)
            }
            IMAGE_BASE.plus(LOGO_SIZE).plus(path)
        }
    }

    fun getFullAvatarPath(author: AuthorDetails): String? {
        return getFullAvatarPath(author.avatarPath)
    }

    fun getFullEpisodeStillPath(path: String?): String? {
        return path?.let {
            IMAGE_BASE.plus(STILL_SIZE).plus(path)
        }
    }

    fun getFullEpisodeStillPath(episode: Episode): String? {
        return getFullEpisodeStillPath(episode.stillPath)
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
        if (series.episodeRuntime.isEmpty()) {
            return ""
        }
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

    fun getMovieRating(releases: List<MovieReleaseResults.ReleaseDateResult>?): String {
        if (releases == null) {
            return ""
        }

        val currentRegion = Locale.current.language
        val certifications = HashMap<String, String>()
        releases.forEach { releaseDateResult ->
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

    fun getTvRating(contentRatings: List<TvContentRatings.TvContentRating>?): String {
        if (contentRatings == null) {
            return ""
        }

        val currentRegion = Locale.current.language
        val certifications = HashMap<String, String>()
        contentRatings.forEach { contentRating ->
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
        return IMAGE_BASE.plus(LOGO_SIZE).plus(path)
    }

    fun releaseYearFromData(releaseDate: String): String {
        if (releaseDate.length >=4) {
            return releaseDate.split("-").first { it.length == 4 }
        }
        return ""
    }

    fun formatRevenue(revenue: Long): String {
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

    fun convertEpisodeDate(inDate: Date?): String? {
        if (inDate == null) {
            return null
        }
        val outFormat = SimpleDateFormat("MMMM dd, yyyy", java.util.Locale.getDefault())
        return outFormat.format(inDate)
    }

    fun toDate(releaseDate: String): Date {
        val format = SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return format.parse(releaseDate) ?: throw FormatException("Expected date format \"yyyy-MM-dd\", got $releaseDate")
    }

}