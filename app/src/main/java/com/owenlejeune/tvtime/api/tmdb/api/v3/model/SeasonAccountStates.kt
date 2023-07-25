package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName

class SeasonAccountStates(
    @SerializedName("id") val id: Int,
    @SerializedName("results") val results: List<SeasonAccountStatesResult>
)

class SeasonAccountStatesResult(
    @SerializedName("id") val id: Int,
    @SerializedName("episode_number") val episodeNumber: Int,
    val isRated: Boolean,
    var rating: Int
)