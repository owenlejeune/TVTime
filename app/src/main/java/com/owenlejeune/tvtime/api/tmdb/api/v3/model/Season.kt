package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Season(
    @SerializedName("_id") val _id: String,
    @SerializedName("air_date") val airDate: Date?,
    @SerializedName("episodes") val episodes: List<Episode>,
    @SerializedName("name") val name: String,
    @SerializedName("overview") val overview: String,
    @SerializedName("id") val id: Int,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("season_number") val seasonNumber: Int
)