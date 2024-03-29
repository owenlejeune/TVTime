package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class BaseSeason(
    @SerializedName("id") val id: Int,
    @SerializedName("air_date") val airDate: Date?,
    @SerializedName("episode_count") val episodeCount: Int,
    @SerializedName("name") val name: String,
    @SerializedName("overview") val overview: String,
    @SerializedName("poster_path") val posterPath: String,
    @SerializedName("season_number") val seasonNumber: Int
)
