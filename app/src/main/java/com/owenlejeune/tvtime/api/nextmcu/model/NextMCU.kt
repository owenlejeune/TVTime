package com.owenlejeune.tvtime.api.nextmcu.model

import com.google.gson.annotations.SerializedName
import com.owenlejeune.tvtime.utils.types.MediaViewType
import java.util.Date

class NextMCU(
    @SerializedName("days_until") val daysUntil: Int,
    @SerializedName("title") val title: String,
    @SerializedName("overview") val overview: String,
    @SerializedName("poster_url") val posterUrl: String?,
    @SerializedName("release_date") val releaseDate: Date,
    @SerializedName("type") val type: MediaViewType,
    @SerializedName("following_production") val followingProduction: NextMCU?
)