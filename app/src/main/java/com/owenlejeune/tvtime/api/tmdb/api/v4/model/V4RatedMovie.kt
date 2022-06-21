package com.owenlejeune.tvtime.api.tmdb.api.v4.model

import com.google.gson.annotations.SerializedName

class V4RatedMovie(
    id: Int,
    overview: String,
    name: String,
    voteAverage: Float,
    voteCount: Int,
    rating: AccountRating,
    backdropPath: String?,
    genreIds: List<Int>,
    originalLanguage: String,
    originalName: String,
    posterPath: String?,
    popularity: Float,
    releaseDate: String,
    @SerializedName("adult") val isAdult: Boolean,
    @SerializedName("video") val video: Boolean
): V4RatedMedia(
    RatedType.MOVIE, id, overview, name, voteAverage, voteCount, rating, releaseDate,
    backdropPath, genreIds, originalLanguage, originalName, posterPath, popularity
)