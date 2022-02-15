package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

open class Person(
    @SerializedName("id") val id: Int,
    @SerializedName("credit_id") val creditId: Int,
    @SerializedName("name") val name: String,
    @SerializedName("gender") val gender: Int
)
