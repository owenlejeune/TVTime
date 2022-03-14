package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName

class DetailPerson(
    @SerializedName("birthday") val birthday: String,
    @SerializedName("known_for_department") val knownFor: String,
    @SerializedName("deathday") val dateOfDeath: String?,
    @SerializedName("biography") val biography: String,
    @SerializedName("place_of_birth") val birthplace: String?,
    @SerializedName("adult") val isAdult: Boolean,
    id: Int,
    name: String,
    gender: Int,
    profilePath: String?
): Person(id, name, gender, profilePath)