package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

abstract class DetailedItem(
    id: Int,
    title: String,
    posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("genres") val genres: List<Genre>,
    @SerializedName("overview") val overview: String?,
    @SerializedName("production_companies") val productionCompanies: List<ProductionCompany>,
    @SerializedName("production_countries") val productionCountries: List<ProductionCountry>,
    @SerializedName("status") val status: Status,
    @SerializedName("tagline") val tagline: String?,
    @SerializedName("vote_average") val voteAverage: Float,
    @SerializedName("vote_count") val voteCount: Int,
    @SerializedName("original_language") val originalLanguage: String,
    @SerializedName("original_name", alternate = ["original_title"]) val originalTitle: String,
    @SerializedName("popularity") val popularity: Float,
    @SerializedName("spoken_languages") val spokenLanguages: List<SpokenLanguage>,
): TmdbItem(id, posterPath, title)