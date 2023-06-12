package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName

data class ConfigurationCountry(
    @SerializedName("iso_3166_1")
    val iso_3166_1: String,
    @SerializedName("english_name")
    val englishName: String,
    @SerializedName("native_name")
    val nativeName: String
)