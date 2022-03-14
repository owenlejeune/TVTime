package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName

class HomePagePeopleResponse(
    @SerializedName("total_results") val totalResults: Int,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("page") val page: Int,
    @SerializedName("results") val results: List<HomePagePerson>
)
