package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class MovieReleaseResults(
    @SerializedName("results") val releaseDates: List<ReleaseDateResult>
) {

    inner class ReleaseDateResult(
        @SerializedName("iso_3166_1") val region: String,
        @SerializedName("release_dates") val releaseDates: List<ReleaseDate>
    )

    inner class ReleaseDate(
        @SerializedName("certification") val certification: String,
        @SerializedName("release_date") val releaseDate: Date?
    )

}
