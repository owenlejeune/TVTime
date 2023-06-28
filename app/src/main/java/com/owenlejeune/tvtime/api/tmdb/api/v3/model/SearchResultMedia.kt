package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName
import com.owenlejeune.tvtime.utils.types.MediaViewType
import java.util.Date

abstract class SearchResultMedia(
    @SerializedName("overview") val overview: String,
    @SerializedName("vote_average") val voteAverage: Float,
    @SerializedName("vote_count") val voteCount: Int,
    @SerializedName("release_date", alternate = ["first_air_date", "air_date"]) val releaseDate: Date?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("genre_ids") val genreIds: List<Int>,
    @SerializedName("original_language") val originalLanguage: String,
    @SerializedName("original_name", alternate = ["original_title"]) val originalName: String,
    posterPath: String?,
    type: MediaViewType,
    id: Int,
    name: String,
    popularity: Float
): SortableSearchResult(type, popularity, id, name, posterPath)