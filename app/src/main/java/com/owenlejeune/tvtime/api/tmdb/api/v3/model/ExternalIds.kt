package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName

data class ExternalIds(
    @SerializedName("facebook_id")
    val facebook: String?,
    @SerializedName("instagram_id")
    val instagram: String?,
    @SerializedName("tiktok_id")
    val tiktok: String?,
    @SerializedName("twitter_id")
    val twitter: String?,
    @SerializedName("youtube_id")
    val youtube: String?
) {
    fun hasExternalIds(): Boolean = (facebook != null)
        .or(instagram != null)
        .or(tiktok != null)
        .or(twitter != null)
        .or(youtube != null)
}