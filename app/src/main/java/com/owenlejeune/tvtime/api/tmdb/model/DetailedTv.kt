package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

class DetailedTv(
    id: Int,
    posterPath: String?,
    title: String,
    backdropPath: String?,
    genres: List<Genre>,
    overview: String?,
    productionCompanies: List<ProductionCompany>,
    productionCountries: List<ProductionCountry>,
    status: Status,
    tagline: String?,
    voteAverage: Float,
    voteCount: Int,
    originalLanguage: String,
    originalName: String,
    popularity: Float,
    spokenLanguages: List<SpokenLanguage>,
    @SerializedName("created_by") val createdBy: List<Person>,
    @SerializedName("first_air_date") val firstAirDate: String,
    @SerializedName("last_air_date") val lastAirDate: String,
    @SerializedName("in_production") val inProduction: Boolean,
    @SerializedName("networks") val networks: List<Network>,
    @SerializedName("number_of_episodes") val numberOfEpisodes: Int,
    @SerializedName("number_of_seasons") val numberOfSeasons: Int,
    @SerializedName("seasons") val seasons: List<BaseSeason>,
    @SerializedName("episode_run_time") val episodeRuntime: List<Int>,
    @SerializedName("languages") val languages: List<String>,
    @SerializedName("last_episode_to_air") val lastEpisodeToAir: BaseEpisode,
    @SerializedName("origin_country") val originCountry: List<String>,
    @SerializedName("type") val type: String
): DetailedItem(
    id, title, posterPath, backdropPath, genres, overview,
    productionCompanies, productionCountries, status, tagline, voteAverage,
    voteCount, originalLanguage, originalName, popularity, spokenLanguages
)