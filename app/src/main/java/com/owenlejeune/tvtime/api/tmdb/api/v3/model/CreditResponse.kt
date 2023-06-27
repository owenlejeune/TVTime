package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName
import com.owenlejeune.tvtime.utils.types.MediaViewType
import java.util.Date

class CreditResponse(
    @SerializedName("credit_type") val creditType: String,
    @SerializedName("department") val department: String,
    @SerializedName("job") val job: String,
    @SerializedName("media") val media: CreditMedia,
    @SerializedName("media_type") val mediaType: MediaViewType,
    @SerializedName("id") val id: String,
    @SerializedName("person") val person: CreditPerson
)

abstract class CreditMedia(
    @SerializedName("adult") val isAdult: Boolean,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("id") val id: Int,
    @SerializedName("title", alternate = ["name"]) val title: String,
    @SerializedName("original_language") val originalLanguage: String,
    @SerializedName("original_title", alternate = ["original_name"]) val originalTitle: String,
    @SerializedName("overview") val overview: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("media_type") val mediaType: MediaViewType,
    @SerializedName("genre_ids") val genreIds: List<Int>,
    @SerializedName("popularity") val popularity: Float,
    @SerializedName("release_date", alternate = ["first_air_date"]) val releaseDate: Date,
    @SerializedName("vote_average") val voteAverage: Float,
    @SerializedName("vote_count") val voteCount: Int,
    @SerializedName("character") val character: String
)

class CreditMovie(
    isAdult: Boolean,
    backdropPath: String?,
    id: Int,
    title: String,
    originalLanguage: String,
    originalTitle: String,
    overview: String,
    posterPath: String?,
    genreIds: List<Int>,
    popularity: Float,
    releaseDate: Date,
    voteAverage: Float,
    voteCount: Int,
    character: String,
    @SerializedName("video") val isVideo: Boolean,
): CreditMedia(isAdult, backdropPath, id, title, originalLanguage, originalTitle, overview, posterPath,
    MediaViewType.MOVIE, genreIds, popularity, releaseDate, voteAverage, voteCount, character)

class CreditTv(
    isAdult: Boolean,
    backdropPath: String?,
    id: Int,
    title: String,
    originalLanguage: String,
    originalTitle: String,
    overview: String,
    posterPath: String?,
    genreIds: List<Int>,
    popularity: Float,
    releaseDate: Date,
    voteAverage: Float,
    voteCount: Int,
    character: String,
    @SerializedName("origin_country") val originCountry: List<String>,
    @SerializedName("episodes") val episodes: List<CreditEpisode>,
    @SerializedName("seasons") val seasons: List<CreditSeason>
): CreditMedia(isAdult, backdropPath, id, title, originalLanguage, originalTitle, overview, posterPath,
    MediaViewType.TV, genreIds, popularity, releaseDate, voteAverage, voteCount, character)

class CreditEpisode(
    @SerializedName("air_date") val airDate: Date,
    @SerializedName("episode_number") val episodeNumber: Int,
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("overview") val overview: String,
    @SerializedName("production_code") val productionCode: String,
    @SerializedName("show_id") val showId: Int,
    @SerializedName("still_path") val stillPath: String?,
    @SerializedName("vote_average") val voteAverage: Float,
    @SerializedName("vote_count") val voteCount: Int
)

class CreditSeason(
    @SerializedName("air_date") val airDate: Date,
    @SerializedName("episode_count") val episodeCount: Int,
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("overview") val overview: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("season_number") val seasonNumber: Int,
    @SerializedName("show_id") val showId: Int
)

class CreditPerson(
    @SerializedName("adult") val isAdult: Boolean,
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("original_name") val originalName: String,
    @SerializedName("popularity") val popularity: Float,
    @SerializedName("gender") val gender: Int, //2 - male
    @SerializedName("known_for_department") val knownForDepartment: String,
    @SerializedName("profile_path") val profilePath: String?
)