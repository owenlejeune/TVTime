package com.owenlejeune.tvtime.api.gotquotes.model

import com.google.gson.annotations.SerializedName

class GotQuote(
    @SerializedName("sentence") val quote: String,
    @SerializedName("character") val character: GotCharacter
)

class GotCharacter(
    @SerializedName("name") val name: String,
    @SerializedName("slug") val slug: String,
    @SerializedName("house") val house: GotHouse
)

class GotHouse(
    @SerializedName("name") val name: String,
    @SerializedName("slug") val slug: String
)