package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName
import com.owenlejeune.tvtime.utils.types.MediaViewType
import java.util.Date

class KnownForTv(
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
    @SerializedName("original_country") val originalCountries: List<String>
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