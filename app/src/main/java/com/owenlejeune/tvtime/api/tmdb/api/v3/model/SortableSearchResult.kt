package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName

abstract class SortableSearchResult(
    @SerializedName("popularity") val popularity: Float,
    @SerializedName("id") val id: Int,
    @SerializedName("name", alternate = ["title"]) val name: String
): Searchable