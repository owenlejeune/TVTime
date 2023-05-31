package com.owenlejeune.tvtime.api.tmdb.api.v4.model

import com.google.gson.annotations.SerializedName

class RecommendedTv(
    id: Int,
    posterPath: String,
    title: String,
    isAdult: Boolean,
    backdropPath: String?,
    originalLanguage: String,
    originalName: String,
    overview: String,
    mediaType: String,
    genreIds: List<Int>,
    popularity: Float,
    releaseDate: String,
    voteAverage: Float,
    voteCount: Int,
    @SerializedName("origin_country")
    val originCountries: List<String>
): RecommendedMedia(
    id, posterPath, title, RecommendedType.SERIES, isAdult, backdropPath, originalLanguage,
    originalName, overview, mediaType, genreIds, popularity, releaseDate, voteAverage, voteCount
)