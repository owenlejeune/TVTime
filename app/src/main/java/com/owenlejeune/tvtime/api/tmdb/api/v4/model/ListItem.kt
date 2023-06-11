package com.owenlejeune.tvtime.api.tmdb.api.v4.model

import com.google.gson.annotations.SerializedName
import com.owenlejeune.tvtime.utils.types.MediaViewType

abstract class ListItem(
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("release_date", alternate = ["first_air_date"]) val releaseDate: String,
    @SerializedName("genre_ids") val genreIds: List<Int>,
    @SerializedName("id") val id: Int,
    @SerializedName("media_type") val mediaType: MediaViewType,
    @SerializedName("title", alternate = ["name"]) val title: String,
    @SerializedName("original_language") val originalLanguage: String,
    @SerializedName("original_title", alternate = ["original_name"]) val originalTitle: String,
    @SerializedName("overview") val overview: String,
    @SerializedName("popularity") val popularity: Float,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("vote_average") val voteAverage: Float,
    @SerializedName("vote_count") val voteCount: Int
)
