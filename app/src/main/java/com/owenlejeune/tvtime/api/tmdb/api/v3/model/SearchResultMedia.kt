package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName
import com.owenlejeune.tvtime.ui.screens.main.MediaViewType

abstract class SearchResultMedia(
    var type: MediaViewType,
    @SerializedName("overview") val overview: String,
    @SerializedName("vote_average") val voteAverage: Float,
    @SerializedName("vote_count") val voteCount: Int,
    @SerializedName("release_date", alternate = ["first_air_date", "air_date"]) val releaseDate: String,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("genre_ids") val genreIds: List<Int>,
    @SerializedName("original_language") val originalLanguage: String,
    @SerializedName("original_name", alternate = ["original_title"]) val originalName: String,
    @SerializedName("poster_path") val posterPath: String?,
    id: Int,
    name: String,
    popularity: Float
): SortableSearchResult(popularity, id, name)