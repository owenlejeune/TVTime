package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

abstract class RatedMedia(
    var type: RatedType,
    @SerializedName("id") val id: Int,
    @SerializedName("overview") val overview: String,
    @SerializedName("name", alternate = ["title"]) val name: String,
    @SerializedName("vote_average") val voteAverage: Float,
    @SerializedName("vote_count") val voteCount: Int,
    @SerializedName("rating") val rating: Float
) {
    enum class RatedType {
        MOVIE,
        SERIES,
        EPISODE
    }
}
