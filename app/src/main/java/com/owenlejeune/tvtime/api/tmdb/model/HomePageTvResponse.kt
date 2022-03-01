package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

class HomePageTvResponse(
    totalResults: Int,
    totalPages: Int,
    page: Int,
    @SerializedName("results") override val results: List<HomePageTv>
): HomePageResponse(totalResults, totalPages, page, results)