package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

enum class Status {
    @SerializedName("Rumoured")
    RUMOURED,
    @SerializedName("Planned")
    PLANNED,
    @SerializedName("In Production")
    IN_PRODUCTION,
    @SerializedName("Post Production")
    POST_PRODUCTION,
    @SerializedName("Released")
    RELEASED,
    @SerializedName("Canceled")
    CANCELED,
    @SerializedName("Returning Series")
    RETURNING_SERIES,
    @SerializedName("Ended")
    ENDED,
    @SerializedName("Pilot")
    PILOT,
    @SerializedName("Active")
    ACTIVE
}