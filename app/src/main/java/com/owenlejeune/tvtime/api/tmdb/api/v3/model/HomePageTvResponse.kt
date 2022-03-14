package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName

class HomePageTvResponse(
    totalResults: Int,
    totalPages: Int,
    page: Int,
    @SerializedName("results") override val results: List<HomePageTv>
): HomePageResponse(totalResults, totalPages, page, results)