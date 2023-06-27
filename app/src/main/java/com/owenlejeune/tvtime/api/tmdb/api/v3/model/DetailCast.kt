package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName
import com.owenlejeune.tvtime.utils.types.MediaViewType
import java.util.Date

abstract class DetailCast(
    id: Int,
    backdropPath: String?,
    genreIds: List<Int>,
    originalLanguage: String,
    originalTitle: String,
    overview: String,
    popularity: Float,
    posterPath: String?,
    releaseDate: Date?,
    title: String,
    voteAverage: Float,
    voteCount: Int,
    creditId: String,
    isAdult: Boolean,
    mediaType: MediaViewType,
    @SerializedName("character") val character: String
): CastCrew(id, backdropPath, genreIds, originalLanguage, originalTitle, overview, popularity,
    posterPath, releaseDate, title, voteAverage, voteCount, creditId, isAdult, mediaType)

class MovieCast(
    id: Int,
    isAdult: Boolean,
    backdropPath: String?,
    genreIds: List<Int>,
    originalLanguage: String,
    originalTitle: String,
    overview: String,
    popularity: Float,
    posterPath: String?,
    releaseDate: Date?,
    title: String,
    voteAverage: Float,
    voteCount: Int,
    creditId: String,
    character: String,
    @SerializedName("video") val isVideo: Boolean,
    @SerializedName("order") val order: Int
): DetailCast(id, backdropPath, genreIds, originalLanguage, originalTitle, overview, popularity,
        posterPath, releaseDate, title, voteAverage, voteCount, creditId, isAdult, MediaViewType.MOVIE, character)

class TvCast(
    id: Int,
    isAdult: Boolean,
    backdropPath: String?,
    genreIds: List<Int>,
    originalLanguage: String,
    originalTitle: String,
    overview: String,
    popularity: Float,
    posterPath: String?,
    releaseDate: Date?,
    title: String,
    voteAverage: Float,
    voteCount: Int,
    creditId: String,
    character: String,
    @SerializedName("origin_country") val originCountry: List<String>,
    @SerializedName("episode_count") val episodeCount: Int
): DetailCast(id, backdropPath, genreIds, originalLanguage, originalTitle, overview, popularity,
    posterPath, releaseDate, title, voteAverage, voteCount, creditId, isAdult, MediaViewType.TV, character)