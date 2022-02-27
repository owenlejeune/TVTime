package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

class RatingBody(
    @SerializedName("value") val rating: Float
)
