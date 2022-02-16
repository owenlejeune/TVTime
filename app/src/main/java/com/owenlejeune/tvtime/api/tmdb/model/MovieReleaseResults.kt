package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

data class MovieReleaseResults(
    @SerializedName("results") val releaseDates: List<ReleaseDateResult>
) {

    inner class ReleaseDateResult(
        @SerializedName("iso_3166_1") val region: String,
        @SerializedName("release_dates") val releaseDates: List<ReleaseDate>
    )

    inner class ReleaseDate(
        @SerializedName("certification") val certification: String,
        @SerializedName("release_date") val releaseDate: String
    )

}
