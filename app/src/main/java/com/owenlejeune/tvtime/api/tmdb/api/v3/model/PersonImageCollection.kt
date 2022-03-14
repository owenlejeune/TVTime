package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName

class PersonImageCollection(
    @SerializedName("profiles") val images: List<PersonImage>
)