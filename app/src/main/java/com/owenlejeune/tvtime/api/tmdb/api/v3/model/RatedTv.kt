package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName

class RatedTv(
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
    releaseDate: String,
    @SerializedName("origin_country") val originCountry: List<String>,
): RatedTopLevelMedia(
    RatedType.SERIES, id, overview, name, voteAverage, voteCount, rating, releaseDate,
    backdropPath, genreIds, originalLanguage, originalName, posterPath, popularity
)