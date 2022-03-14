package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName

data class ImageCollection(
    @SerializedName("backdrops") val backdrops: List<Image>,
    @SerializedName("posters") val posters: List<Image>
)
