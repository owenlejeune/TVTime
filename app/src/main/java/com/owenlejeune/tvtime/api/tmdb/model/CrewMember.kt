package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

class CrewMember(
    @SerializedName("department") val department: String,
    @SerializedName("job") val job: String,
    @SerializedName("credit_id") val creditId: String,
    id: Int,
    name: String,
    gender: Int,
    profilePath: String?
): Person(id, name, gender, profilePath)
