package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

class RatedMedia(
    @SerializedName("name", alternate = ["title"]) val title: String,
    @SerializedName("id") val id: Int,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("release_date", alternate = ["first_air_date", "air_date"]) val releaseDate: String,
    @SerializedName("rating") val rating: Float,
    @SerializedName("genre_ids") val genreIds: List<Int>,
    @SerializedName("original_language") val originalLanguage: String,
    @SerializedName("original_title") val originalTitle: String,
    @SerializedName("overview") val overview: String,
    @SerializedName("popularity") val popularity: Float,
    @SerializedName("vote_average") val voteAverage: Float,
    @SerializedName("vote_count") val voteCount: Int,
    // only for movies
    @SerializedName("adult") val isAdult: Boolean?,
    @SerializedName("video") val isVideo: Boolean?,
    // only for tv
    @SerializedName("origin_country") val originCountries: List<String>?,

    var type: Type
) {

    enum class Type {
        MOVIE,
        SERIES,
        EPISODE
    }

}
