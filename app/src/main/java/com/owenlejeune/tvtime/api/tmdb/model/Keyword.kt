package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

class Keyword(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)
