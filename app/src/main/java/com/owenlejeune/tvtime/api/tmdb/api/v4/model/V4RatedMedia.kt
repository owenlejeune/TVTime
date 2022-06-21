package com.owenlejeune.tvtime.api.tmdb.api.v4.model

import com.google.gson.annotations.SerializedName

abstract class V4RatedMedia(
    var type: RatedType,
    @SerializedName("id") val id: Int,
    @SerializedName("overview") val overview: String,
    @SerializedName("name", alternate = ["title"]) val name: String,
    @SerializedName("vote_average") val voteAverage: Float,
    @SerializedName("vote_count") val voteCount: Int,
    @SerializedName("rating") val rating: AccountRating,
    @SerializedName("release_date", alternate = ["first_air_date", "air_date"]) val releaseDate: String,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("genre_ids") val genreIds: List<Int>,
    @SerializedName("original_language") val originalLanguage: String,
    @SerializedName("original_name", alternate = ["original_title"]) val originalName: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("popularity") val popularity: Float
) {
    enum class RatedType {
        MOVIE,
        SERIES,
//        EPISODE
    }

    inner class AccountRating(
        @SerializedName("value") val rating: Int,
        @SerializedName("created_at") val createdAt: String
    )
}