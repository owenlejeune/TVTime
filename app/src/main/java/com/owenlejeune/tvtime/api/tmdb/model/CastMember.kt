package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

class CastMember(
    @SerializedName("character") val character: String,
    @SerializedName("order") val order: Int,
    id: Int,
    creditId: String,
    name: String,
    gender: Int,
    profilePath: String?
): Person(id, creditId, name, gender, profilePath)