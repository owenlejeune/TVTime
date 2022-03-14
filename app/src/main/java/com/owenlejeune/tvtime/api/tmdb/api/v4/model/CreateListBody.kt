package com.owenlejeune.tvtime.api.tmdb.api.v4.model

import com.google.gson.annotations.SerializedName

class CreateListBody(
    @SerializedName("name") val name: String,
    @SerializedName("iso_639_1") val language: String,
    @SerializedName("description") val description: String,
    @SerializedName("public") val isPublic: Boolean,
    @SerializedName("iso_3166_1") val localeCode: String
)
