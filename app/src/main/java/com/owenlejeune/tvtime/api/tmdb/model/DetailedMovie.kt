package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

class DetailedMovie(
    id: Int,
    posterPath: String?,
    @SerializedName("title") override val title: String,
    @SerializedName("backdrop_path") override val backdropPath: String?,
    @SerializedName("genres") override val genres: List<Genre>,
    @SerializedName("overview") override val overview: String?,
    @SerializedName("production_companies") override val productionCompanies: List<ProductionCompany>,
    @SerializedName("status") override val status: Status,
    @SerializedName("tagline") override val tagline: String?,
    @SerializedName("vote_average") override val voteAverage: Float,
    @SerializedName("adult") val isAdult: Boolean,
    @SerializedName("budget") val budget: Int,
    @SerializedName("release_date") val releaseDate: String,
    @SerializedName("revenue") val revenue: Int,
    @SerializedName("runtime") val runtime: Int?,
    @SerializedName("imdb_id") val imdbId: String?,
    @SerializedName("original_language") val originalLanguage: String,
    @SerializedName("popularity") val popularity: Float,
    @SerializedName("production_countries") val productionCountries: List<ProductionCountry>,
    @SerializedName("spoken_languages") val spokenLanguages: List<SpokenLanguage>,
    @SerializedName("original_title") val originalTitle: String,
    @SerializedName("vote_count") val voteCount: Int
): DetailedItem(id, title, posterPath, backdropPath, genres, overview, productionCompanies, status, tagline, voteAverage)
