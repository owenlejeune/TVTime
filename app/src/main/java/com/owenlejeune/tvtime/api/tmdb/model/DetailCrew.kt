package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

class DetailCrew(
    @SerializedName("id") val id: Int,
    @SerializedName("department") val department: String,
    @SerializedName("episode_count") val episodeCount: Int,
    @SerializedName("job") val job: String,
    @SerializedName("overview") val overview: String,
    @SerializedName("name") val name: String,
    @SerializedName("media_type") val mediaType: String,
    @SerializedName("first_air_date") val firstAirDate: String,
    @SerializedName("poster_path") val posterPath: String,
    @SerializedName("title") val title: String,
    @SerializedName("adult") val isAdult: Boolean,
    @SerializedName("release_date") val releaseDate: String
)