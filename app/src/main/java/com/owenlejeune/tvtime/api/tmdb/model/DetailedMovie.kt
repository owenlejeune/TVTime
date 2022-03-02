package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

class DetailedMovie(
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
    originalTitle: String,
    popularity: Float,
    spokenLanguages: List<SpokenLanguage>,
    @SerializedName("adult") val isAdult: Boolean,
    @SerializedName("budget") val budget: Int,
    @SerializedName("release_date") val releaseDate: String,
    @SerializedName("revenue") val revenue: Int,
    @SerializedName("runtime") val runtime: Int?,
    @SerializedName("imdb_id") val imdbId: String?,
): DetailedItem(
    id, title, posterPath, backdropPath, genres, overview,
    productionCompanies, productionCountries, status, tagline, voteAverage,
    voteCount, originalLanguage, originalTitle, popularity, spokenLanguages
)
