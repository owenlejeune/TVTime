package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName

data class Image(
    @SerializedName("file_path") val filePath: String,
    @SerializedName("height") val height: Int,
    @SerializedName("width") val width: Int,
    @SerializedName("aspect_ratio") val aspectRatio: Float? = null,
    @SerializedName("iso_639_1") val language: String? = null
)