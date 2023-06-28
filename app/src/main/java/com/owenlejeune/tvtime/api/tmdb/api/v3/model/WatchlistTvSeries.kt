package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName
import java.util.Date

class WatchlistTvSeries(
    posterPath: String?,
    popularity: Float,
    id: Int,
    backdropPath: String?,
    voteAverage: Float,
    overview: String,
    releaseDate: Date?,
    genreIds: List<Int>,
    originalLanguage: String,
    voteCount: Int,
    title: String,
    originalTitle: String,
    @SerializedName("origin_country") val originCountry: List<String>
): WatchlistMedia(
    posterPath, popularity, id, backdropPath, voteAverage, overview, releaseDate,
    genreIds, originalLanguage, voteCount, title, originalTitle
)