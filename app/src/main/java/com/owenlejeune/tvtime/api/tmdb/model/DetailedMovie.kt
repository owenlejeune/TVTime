package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

class DetailedMovie(
    @SerializedName("id") override val id: Int,
    @SerializedName("original_title") override val title: String,
    @SerializedName("poster_path") override val posterPath: String?,
    @SerializedName("backdrop_path") override val backdropPath: String?,
    @SerializedName("genres") override val genres: List<Genre>,
    @SerializedName("overview") override val overview: String?,
    @SerializedName("production_companies") override val productionCompanies: List<ProductionCompany>,
    @SerializedName("status") override val status: String,
    @SerializedName("tagline") override val tagline: String?,
    @SerializedName("vote_average") override val voteAverage: Float,
    @SerializedName("adult") val isAdult: Boolean,
    @SerializedName("budget") val budget: Int,
    @SerializedName("release_date") val releaseDate: String,
    @SerializedName("revenue") val revenue: Int,
    @SerializedName("runtime") val runtime: Int?
): DetailedItem(id, title, posterPath, backdropPath, genres, overview, productionCompanies, status, tagline, voteAverage)
