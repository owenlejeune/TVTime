package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

class RatedMovie(
    id: Int,
    overview: String,
    name: String,
    voteAverage: Float,
    voteCount: Int,
    rating: Float,
    backdropPath: String?,
    genreIds: List<Int>,
    originalLanguage: String,
    originalName: String,
    posterPath: String?,
    popularity: Float,
    @SerializedName("adult") val isAdult: Boolean,
    @SerializedName("release_date") val releaseDate: String,
    @SerializedName("video") val video: Boolean,
): RatedTopLevelMedia(
    RatedType.MOVIE, id, overview, name, voteAverage, voteCount, rating,
    backdropPath, genreIds, originalLanguage, originalName, posterPath, popularity
)