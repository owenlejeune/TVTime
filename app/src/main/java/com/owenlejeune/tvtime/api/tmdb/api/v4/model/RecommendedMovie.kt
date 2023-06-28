package com.owenlejeune.tvtime.api.tmdb.api.v4.model

import com.google.gson.annotations.SerializedName
import java.util.Date

class RecommendedMovie (
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
    releaseDate: Date?,
    voteAverage: Float,
    voteCount: Int,
    @SerializedName("video")
    val isVideo: Boolean
): RecommendedMedia(
    id, posterPath, title, RecommendedType.MOVIE, isAdult, backdropPath, originalLanguage,
    originalName, overview, mediaType, genreIds, popularity, releaseDate, voteAverage, voteCount
)