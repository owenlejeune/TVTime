package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName
import com.owenlejeune.tvtime.ui.screens.main.MediaViewType

class SearchResultTv(
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
    @SerializedName("origin_country") val originCountry: List<String>,
): SearchResultMedia(
    overview, voteAverage, voteCount, releaseDate, backdropPath, genreIds,
    originalLanguage, originalName, posterPath, MediaViewType.TV, id, name, popularity
)