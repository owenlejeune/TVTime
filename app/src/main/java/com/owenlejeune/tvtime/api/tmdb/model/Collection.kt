package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

class Collection(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?
)