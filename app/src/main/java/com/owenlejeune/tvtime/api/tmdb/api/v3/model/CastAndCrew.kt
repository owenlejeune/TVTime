package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName

class CastAndCrew(
    @SerializedName("cast") val cast: List<CastMember>,
    @SerializedName("crew") val crew: List<CrewMember>
)