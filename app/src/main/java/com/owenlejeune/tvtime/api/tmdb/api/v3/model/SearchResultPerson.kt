package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName
import com.owenlejeune.tvtime.utils.types.MediaViewType

class SearchResultPerson(
    @SerializedName("profile_path") val profilePath: String,
    @SerializedName("adult") val isAdult: Boolean,
    @SerializedName("known_for") val knownFor: List<KnownFor>,
    id: Int,
    name: String,
    popularity: Float
): SortableSearchResult(MediaViewType.PERSON, popularity, id, name)