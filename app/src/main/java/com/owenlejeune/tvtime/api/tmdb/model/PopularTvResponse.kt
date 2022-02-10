package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

data class PopularTvResponse(
    @SerializedName("total_results") val count: Int,
    @SerializedName("page") val page: Int,
    @SerializedName("results") val tv: List<PopularTv>
)