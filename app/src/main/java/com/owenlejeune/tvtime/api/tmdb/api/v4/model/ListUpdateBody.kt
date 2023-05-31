package com.owenlejeune.tvtime.api.tmdb.api.v4.model

import com.google.gson.annotations.SerializedName

class ListUpdateBody(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("public") val isPublic: Boolean,
    @SerializedName("sort_by") val sortBy: SortOrder
)
