package com.owenlejeune.tvtime.api.tmdb.api.v4.model

import com.google.gson.annotations.SerializedName

class V4RatedTv(
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
    @SerializedName("origin_country") val originCountry: List<String>,
): V4RatedMedia(
    RatedType.SERIES, id, overview, name, voteAverage, voteCount, rating, releaseDate,
    backdropPath, genreIds, originalLanguage, originalName, posterPath, popularity
)