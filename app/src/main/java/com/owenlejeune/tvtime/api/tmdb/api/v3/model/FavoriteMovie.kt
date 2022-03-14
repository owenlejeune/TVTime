package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName

class FavoriteMovie(
    posterPath: String?,
    popularity: Float,
    id: Int,
    backdropPath: String?,
    voteAverage: Float,
    overview: String,
    releaseDate: String,
    genreIds: List<Int>,
    originalLanguage: String,
    voteCount: Int,
    title: String,
    originalTitle: String,
    @SerializedName("adult") val isAdult: Boolean,
    @SerializedName("video") val isVideo: Boolean,
): FavoriteMedia(
    posterPath, popularity, id, backdropPath, voteAverage, overview, releaseDate,
    genreIds, originalLanguage, voteCount, title, originalTitle
)
