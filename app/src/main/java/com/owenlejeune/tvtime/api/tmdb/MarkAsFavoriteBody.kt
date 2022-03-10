package com.owenlejeune.tvtime.api.tmdb

import com.google.gson.annotations.SerializedName

class MarkAsFavoriteBody(
    @SerializedName("media_type") val mediaType: String, // media type
    @SerializedName("media_id") val mediaId: Int,
    @SerializedName("favorite") val isFavorite: Boolean
)
