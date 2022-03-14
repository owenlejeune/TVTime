package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName
import com.owenlejeune.tvtime.ui.screens.MediaViewType

class DetailCast(
    @SerializedName("id") val id: Int,
    @SerializedName("episode_count") val episodeCount: Int,
    @SerializedName("overview") val overview: String,
    @SerializedName("name", alternate = ["title"]) val name: String,
    @SerializedName("media_type") val mediaType: MediaViewType,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("first_air_date") val firstAirDate: String,
    @SerializedName("character") val character: String,
    @SerializedName("adult") val isAdult: Boolean,
    @SerializedName("release_date") val releaseDate: String
)
