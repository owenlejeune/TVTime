package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

class RatedMediaResponse(
    @SerializedName("page") val page: Int,
    @SerializedName("results") val results: List<RatedMedia>,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int
)