package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

class Video(
    @SerializedName("iso_639_1") val language: String,
    @SerializedName("iso_3166_1") val region: String,
    @SerializedName("name") val name: String,
    @SerializedName("key") val key: String,
    @SerializedName("site") val site: String,
    @SerializedName("size") val size: Int,
    @SerializedName("type") val type: String,
    @SerializedName("official") val isOfficial: Boolean
)