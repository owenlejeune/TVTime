package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

class PersonImageCollection(
    @SerializedName("profiles") val images: List<PersonImage>
)