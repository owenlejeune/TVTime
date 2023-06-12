package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName

data class ConfigurationTimezone(
    @SerializedName("iso_3166_1")
    val iso_3166_1: String,
    @SerializedName("zones")
    val zones: List<String>
)