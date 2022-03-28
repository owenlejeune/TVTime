package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName

class SearchResultPerson(
    @SerializedName("profile_path") val profilePath: String,
    @SerializedName("adult") val isAdult: Boolean,
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("known_for") val knownFor: List<KnownFor>,
    popularity: Float
): SortableSearchResult(popularity)