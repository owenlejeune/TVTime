package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName
import com.owenlejeune.tvtime.utils.types.MediaViewType

abstract class SortableSearchResult(
    @SerializedName("media_type") val mediaType: MediaViewType,
    @SerializedName("popularity") val popularity: Float,
    id: Int,
    name: String,
    posterPath: String?
): TmdbItem(id, posterPath, name), Searchable