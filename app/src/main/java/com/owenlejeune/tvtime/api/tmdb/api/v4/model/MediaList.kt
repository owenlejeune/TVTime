package com.owenlejeune.tvtime.api.tmdb.api.v4.model

import com.google.gson.annotations.SerializedName

class MediaList(
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("id") val id: Int,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("total_results") val totalResults: Int,
    @SerializedName("public") val isPublic: Int,
    @SerializedName("results") val results: List<ListItem>,
    @SerializedName("iso_639_1") val language: String,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("description") val description: String,
    @SerializedName("created_by") val createdBy: ListAuthor,
    @SerializedName("iso_3166_1") val localeCode: String,
    @SerializedName("average_rating") val averageRating: Float,
    @SerializedName("runtime") val runtime: Int,
    @SerializedName("name") val name: String
)