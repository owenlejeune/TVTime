package com.owenlejeune.tvtime.api.tmdb.api.v4.model

import com.google.gson.annotations.SerializedName
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.TmdbItem
import java.util.Date

abstract class RecommendedMedia(
    id: Int,
    posterPath: String,
    title: String,
    val type: RecommendedType,
    @SerializedName("adult")
    val isAdult: Boolean,
    @SerializedName("backdrop_path")
    val backdropPath: String?,
    @SerializedName("original_language")
    val originalLanguage: String,
    @SerializedName("original_title", alternate = ["original_name"])
    val originalName: String,
    @SerializedName("overview")
    val overview: String,
    @SerializedName("media_type")
    val mediaType: String,
    @SerializedName("genre_ids")
    val genreIds: List<Int>,
    @SerializedName("popularity")
    val popularity: Float,
    @SerializedName("release_date", alternate = ["first_air_date"])
    val releaseDate: Date?,
    @SerializedName("vote_average")
    val voteAverage: Float,
    @SerializedName("vote_count")
    val voteCount: Int
): TmdbItem(id, posterPath, title) {

    enum class RecommendedType {
        MOVIE,
        SERIES
    }
}