package com.owenlejeune.tvtime.api.tmdb.api.v4.model

import com.google.gson.annotations.SerializedName

class V4AccountResponse<T>(
    @SerializedName("page") val page: Int,
    @SerializedName("total_results") val totalResults: Int,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("results") val results: List<T>
)