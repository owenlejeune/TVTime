package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName

class PersonImage(
    @SerializedName("aspect_ratio") val aspectRatio: Float,
    @SerializedName("file_path") val filePath: String,
    @SerializedName("width") val width: Int,
    @SerializedName("height") val height: Int
)