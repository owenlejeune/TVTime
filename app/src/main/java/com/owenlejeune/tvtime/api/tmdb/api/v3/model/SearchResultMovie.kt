package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName

class SearchResultMovie(
    id: Int,
    overview: String,
    name: String,
    voteAverage: Float,
    voteCount: Int,
    backdropPath: String?,
    genreIds: List<Int>,
    originalLanguage: String,
    originalName: String,
    posterPath: String?,
    popularity: Float,
    releaseDate: String,
    @SerializedName("adult") val isAdult: Boolean,
    @SerializedName("video") val video: Boolean,
): SearchResultMedia(
    SearchResultType.MOVIE, id, overview, name, voteAverage, voteCount, releaseDate,
    backdropPath, genreIds, originalLanguage, originalName, posterPath, popularity
)