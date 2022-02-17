package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

abstract class HomePageResponse(
    @SerializedName("total_results") val count: Int,
    @SerializedName("page") val page: Int,
    @Transient open val results: List<TmdbItem>
)