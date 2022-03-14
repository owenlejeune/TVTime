package com.owenlejeune.tvtime.api.tmdb.api.v4.model

import com.google.gson.annotations.SerializedName

class ListUpdateBody(
    @SerializedName("description") val description: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("public") val isPublic: Boolean?
//    @SerializedName("sort_by")
)
