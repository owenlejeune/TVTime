package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

class CastMember(
    @SerializedName("character") val character: String,
    @SerializedName("order") val order: Int,
    @SerializedName("credit_id") val creditId: String,
    id: Int,
    name: String,
    gender: Int,
    profilePath: String?
): Person(id, name, gender, profilePath)