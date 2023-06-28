package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName
import com.owenlejeune.tvtime.utils.types.MediaViewType
import java.util.Date

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
    releaseDate: Date?,
    @SerializedName("adult") val isAdult: Boolean,
    @SerializedName("video") val video: Boolean,
): SearchResultMedia(
    overview, voteAverage, voteCount, releaseDate, backdropPath, genreIds,
    originalLanguage, originalName, posterPath, MediaViewType.MOVIE, id, name, popularity
)