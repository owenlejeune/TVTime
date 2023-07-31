package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName
import com.owenlejeune.tvtime.utils.types.Gender

open class Person(
    @SerializedName("id") val id: Int,
    @SerializedName("name", alternate = ["title"]) val name: String,
    @SerializedName("gender") val gender: Gender,
    @SerializedName("profile_path") val profilePath: String?
)
