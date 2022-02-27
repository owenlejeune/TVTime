package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

class RatedMediaResponse(
    @SerializedName("results") val results: List<RatedMedia>
)
