package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName

abstract class RatedTopLevelMedia(
    type: RatedType,
    id: Int,
    overview: String,
    name: String,
    voteAverage: Float,
    voteCount: Int,
    rating: Float,
    releaseDate: String,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("genre_ids") val genreIds: List<Int>,
    @SerializedName("original_language") val originalLanguage: String,
    @SerializedName("original_name", alternate = ["original_title"]) val originalName: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("popularity") val popularity: Float,
): RatedMedia(type, id, overview, name, voteAverage, voteCount, rating, releaseDate)