package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName
import com.owenlejeune.tvtime.utils.types.MediaViewType
import java.util.Date

abstract class CastCrew(
    @SerializedName("id") val id: Int,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("genre_ids") val genreIds: List<Int>,
    @SerializedName("original_language") val originalLanguage: String,
    @SerializedName("original_title", alternate = ["original_name"]) val originalTitle: String,
    @SerializedName("overview") val overview: String,
    @SerializedName("popularity") val popularity: Float,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("release_date", alternate = ["first_air_date"]) val releaseDate: Date?,
    @SerializedName("title", alternate = ["name"]) val title: String,
    @SerializedName("vote_average") val voteAverage: Float,
    @SerializedName("vote_count") val voteCount: Int,
    @SerializedName("credit_id") val creditId: String,
    @SerializedName("adult") val isAdult: Boolean,
    @SerializedName("media_type") val mediaType: MediaViewType
)