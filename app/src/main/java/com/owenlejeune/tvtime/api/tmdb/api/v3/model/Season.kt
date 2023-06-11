package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName

data class Season(
    @SerializedName("name")
    val name: String,
    @SerializedName("episodes")
    val episodes: List<Episode>
)