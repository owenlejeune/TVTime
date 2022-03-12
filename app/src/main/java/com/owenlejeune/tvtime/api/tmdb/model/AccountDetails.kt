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
    @SerializedName("gravatar") val gravatar: Gravatar?,
    @SerializedName("tmdb") val tmdb: TmdbAvatar?
)

class Gravatar(
    @SerializedName("hash") val hash: String?
) {
    companion object {
        private const val DEF_HASH = "88c8a9052642ec51d85d4f7beb178a97"
    }
    fun isDefault() = hash?.equals(DEF_HASH)
}

class TmdbAvatar(
    @SerializedName("avatar_path") val avatarPath: String?
)
