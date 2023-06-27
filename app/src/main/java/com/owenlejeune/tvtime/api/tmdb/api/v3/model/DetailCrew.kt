package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName
import com.owenlejeune.tvtime.utils.types.MediaViewType
import java.util.Date

abstract class DetailCrew(
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
    @SerializedName("department") val department: String,
    @SerializedName("job") val job: String,
): CastCrew(id, backdropPath, genreIds, originalLanguage, originalTitle, overview, popularity,
    posterPath, releaseDate, title, voteAverage, voteCount, creditId, isAdult, mediaType)

class MovieCrew(
    id: Int,
    backdropPath: String?,
    genreIds: List<Int>,
    originalLanguage: String,
    isAdult: Boolean,
    originalTitle: String,
    overview: String,
    popularity: Float,
    posterPath: String?,
    releaseDate: Date?,
    title: String,
    voteAverage: Float,
    voteCount: Int,
    creditId: String,
    department: String,
    job: String,
    @SerializedName("video") val isVideo: Boolean
): DetailCrew(id, backdropPath, genreIds, originalLanguage, originalTitle, overview, popularity, posterPath,
    releaseDate, title, voteAverage, voteCount, creditId, isAdult, MediaViewType.MOVIE, department, job)

class TvCrew(
    id: Int,
    backdropPath: String?,
    genreIds: List<Int>,
    originalLanguage: String,
    isAdult: Boolean,
    originalTitle: String,
    overview: String,
    popularity: Float,
    posterPath: String?,
    releaseDate: Date?,
    title: String,
    voteAverage: Float,
    voteCount: Int,
    creditId: String,
    department: String,
    job: String,
    @SerializedName("original_country") val originalCountry: List<String>,
    @SerializedName("episode_count") val episodeCount: Int
): DetailCrew(id, backdropPath, genreIds, originalLanguage, originalTitle, overview, popularity, posterPath,
    releaseDate, title, voteAverage, voteCount, creditId, isAdult, MediaViewType.TV, department, job)