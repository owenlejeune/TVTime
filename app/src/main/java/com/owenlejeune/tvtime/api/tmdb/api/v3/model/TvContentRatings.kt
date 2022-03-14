package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName

class TvContentRatings(
    @SerializedName("results") val results: List<TvContentRating>
) {

    inner class TvContentRating(
        @SerializedName("iso_3166_1") val language: String,
        @SerializedName("rating") val rating: String
    )

}