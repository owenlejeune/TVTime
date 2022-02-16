package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

class DetailedTv(
    @SerializedName("id") override val id: Int,
    @SerializedName("name") override val title: String,
    @SerializedName("poster_path") override val posterPath: String?,
    @SerializedName("backdrop_path") override val backdropPath: String?,
    @SerializedName("genres") override val genres: List<Genre>,
    @SerializedName("overview") override val overview: String?,
    @SerializedName("production_companies") override val productionCompanies: List<ProductionCompany>,
    @SerializedName("status") override val status: String,
    @SerializedName("tagline") override val tagline: String?,
    @SerializedName("vote_average") override val voteAverage: Float,
    @SerializedName("created_by") val createdBy: List<Person>,
    @SerializedName("first_air_date") val firstAirDate: String,
    @SerializedName("last_air_date") val lastAirDate: String,
    @SerializedName("in_production") val inProduction: Boolean,
    @SerializedName("networks") val networks: List<Network>,
    @SerializedName("number_of_episodes") val numberOfEpisodes: Int,
    @SerializedName("number_of_seasons") val numberOfSeasons: Int,
    @SerializedName("seasons") val seasons: List<Season>,
    @SerializedName("episode_run_time") val episodeRuntime: List<Int>
): DetailedItem(id, title, posterPath, backdropPath, genres, overview, productionCompanies, status, tagline, voteAverage)