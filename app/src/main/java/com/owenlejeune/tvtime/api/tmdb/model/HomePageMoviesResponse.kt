package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

class HomePageMoviesResponse(
    count: Int,
    page: Int,
    @SerializedName("results") override val results: List<HomePageMovie>
): HomePageResponse(count, page, results)