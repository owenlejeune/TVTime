package com.owenlejeune.tvtime.api.tmdb.api.v4.model

import com.google.gson.annotations.SerializedName
import com.owenlejeune.tvtime.ui.screens.MediaViewType

class ListTv(
    backdropPath: String?,
    releaseDate: String,
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
    @SerializedName("origin_country") val originCountries: List<String>
): ListItem(
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