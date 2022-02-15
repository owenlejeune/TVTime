package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

class CrewMember(
    @SerializedName("department") val department: String,
    @SerializedName("job") val job: String,
    id: Int,
    creditId: String,
    name: String,
    gender: Int,
    profilePath: String?
): Person(id, creditId, name, gender, profilePath)
