package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

class DetailCast(
    @SerializedName("id") val id: Int,
    @SerializedName("episode_count") val episodeCount: Int,
    @SerializedName("overview") val overview: String,
    @SerializedName("name") val name: String,
    @SerializedName("media_type") val mediaType: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("first_air_date") val firstAirDate: String,
    @SerializedName("character") val character: String,
    @SerializedName("title") val title: String,
    @SerializedName("adult") val isAdult: Boolean,
    @SerializedName("release_date") val releaseDate: String
)
