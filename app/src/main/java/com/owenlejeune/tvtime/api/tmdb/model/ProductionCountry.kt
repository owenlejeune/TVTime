package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

class ProductionCountry(
    @SerializedName("iso_3166_1") val language: String,
    @SerializedName("name") val name: String
)
