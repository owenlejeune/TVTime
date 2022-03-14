package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName

abstract class RatedMedia(
    var type: RatedType,
    @SerializedName("id") val id: Int,
    @SerializedName("overview") val overview: String,
    @SerializedName("name", alternate = ["title"]) val name: String,
    @SerializedName("vote_average") val voteAverage: Float,
    @SerializedName("vote_count") val voteCount: Int,
    @SerializedName("rating") val rating: Float,
    @SerializedName("release_date", alternate = ["first_air_date", "air_date"]) val releaseDate: String
) {
    enum class RatedType {
        MOVIE,
        SERIES,
        EPISODE
    }
}
