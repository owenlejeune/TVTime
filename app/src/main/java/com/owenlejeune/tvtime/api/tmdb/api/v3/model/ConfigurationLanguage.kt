package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName

data class ConfigurationLanguage(
    @SerializedName("iso_639_1")
    val iso_639_1: String,
    @SerializedName("english_name")
    val englishName: String,
    @SerializedName("name")
    val name: String
)