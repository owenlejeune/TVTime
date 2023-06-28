package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName
import java.util.Date

abstract class FavoriteMedia(
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("popularity") val popularity: Float,
    @SerializedName("id") val id: Int,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("vote_average") val voteAverage: Float,
    @SerializedName("overview") val overview: String,
    @SerializedName("release_date", alternate = ["first_air_date"]) val releaseDate: Date?,
    @SerializedName("genre_ids") val genreIds: List<Int>,
    @SerializedName("original_language") val originalLanguage: String,
    @SerializedName("vote_count") val voteCount: Int,
    @SerializedName("title", alternate = ["name"]) val title: String,
    @SerializedName("original_title", alternate = ["original_name"]) val originalTitle: String
)