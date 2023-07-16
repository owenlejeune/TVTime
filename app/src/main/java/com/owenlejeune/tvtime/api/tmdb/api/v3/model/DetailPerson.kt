package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName
import com.owenlejeune.tvtime.utils.types.Gender
import java.util.Date

class DetailPerson(
    @SerializedName("birthday") val birthday: Date,
    @SerializedName("known_for_department") val knownFor: String,
    @SerializedName("deathday") val dateOfDeath: Date?,
    @SerializedName("biography") val biography: String,
    @SerializedName("place_of_birth") val birthplace: String?,
    @SerializedName("adult") val isAdult: Boolean,
    @SerializedName("also_known_as") val alsoKnownAs: List<String>,
    @SerializedName("homepage") val homepage: String?,
    @SerializedName("popularity") val popularity: Float,
    id: Int,
    name: String,
    gender: Gender,
    profilePath: String?
): Person(id, name, gender, profilePath)