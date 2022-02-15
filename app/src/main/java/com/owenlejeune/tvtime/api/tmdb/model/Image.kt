package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

data class Image(
    @SerializedName("file_path") val filePath: String,
    @SerializedName("height") val height: Int,
    @SerializedName("width") val width: Int
)