package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

class HomePageMoviesResponse(
    totalResults: Int,
    totalPages: Int,
    page: Int,
    @SerializedName("results") override val results: List<HomePageMovie>
): HomePageResponse(totalResults, totalPages, page, results)