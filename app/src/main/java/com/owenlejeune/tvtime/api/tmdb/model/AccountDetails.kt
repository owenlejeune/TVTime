package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

class AccountDetails(
    @SerializedName("avatar") val avatar: Avatar,
    @SerializedName("id") val id: Int,
    @SerializedName("iso_639_1") val languageCode: String,
    @SerializedName("iso_3166_1") val countryCode: String,
    @SerializedName("name") val name: String,
    @SerializedName("include_adult") val includeAdult: Boolean,
    @SerializedName("username") val username: String
)

class Avatar(
    @SerializedName("gravatar") val gravatar: Gravatar
)

class Gravatar(
    @SerializedName("hash") val hash: String
)
