package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

class SpokenLanguage(
    @SerializedName("iso_639_1") val language: String,
    @SerializedName("name") val name: String
)
