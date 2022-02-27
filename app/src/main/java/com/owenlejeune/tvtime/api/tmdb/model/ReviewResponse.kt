package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

class ReviewResponse(
    @SerializedName("page") val page: Int,
    @SerializedName("results") val results: List<Review>
)