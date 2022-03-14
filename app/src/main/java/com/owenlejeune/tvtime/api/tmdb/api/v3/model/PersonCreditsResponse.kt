package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName

class PersonCreditsResponse(
    @SerializedName("cast") val cast: List<DetailCast>,
    @SerializedName("crew") val crew: List<DetailCrew>
)