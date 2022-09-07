package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName

class SearchResultPerson(
    @SerializedName("profile_path") val profilePath: String,
    @SerializedName("adult") val isAdult: Boolean,
    @SerializedName("known_for") val knownFor: List<KnownFor>,
    id: Int,
    name: String,
    popularity: Float
): SortableSearchResult(popularity, id, name)