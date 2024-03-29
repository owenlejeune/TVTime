package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName
import com.owenlejeune.tvtime.utils.types.MediaViewType
import java.util.Date

class KnownForMovie(
    backdropPath: String?,
    releaseDate: Date?,
    genreIds: List<Int>,
    id: Int,
    mediaType: MediaViewType,
    title: String,
    originalLanguage: String,
    originalTitle: String,
    overview: String,
    popularity: Float,
    posterPath: String?,
    voteAverage: Float,
    voteCount: Int,
    @SerializedName("video") val isVideo: Boolean,
    @SerializedName("adult") val isAdult: Boolean
) : KnownFor(
    backdropPath,
    releaseDate,
    genreIds,
    id,
    mediaType,
    title,
    originalLanguage,
    originalTitle,
    overview,
    popularity,
    posterPath,
    voteAverage,
    voteCount
)