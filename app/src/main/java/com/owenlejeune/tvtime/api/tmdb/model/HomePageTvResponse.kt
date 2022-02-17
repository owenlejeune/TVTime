package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

class HomePageTvResponse(
    count: Int,
    page: Int,
    @SerializedName("results") override val results: List<HomePageTv>
): HomePageResponse(count, page, results)