package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName

class HomePagePerson(
    @SerializedName("profile_path") val profilePath: String,
    @SerializedName("adult") val isAdult: Boolean,
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("popularity") val popularity: Float
//    @SerializedName("known_for")
)