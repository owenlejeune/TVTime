package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName
import com.owenlejeune.tvtime.R

enum class Status(val stringRes: Int) {
    @SerializedName("Rumoured")
    RUMOURED(R.string.status_rumoured),
    @SerializedName("Planned")
    PLANNED(R.string.status_planned),
    @SerializedName("In Production")
    IN_PRODUCTION(R.string.status_in_production),
    @SerializedName("Post Production")
    POST_PRODUCTION(R.string.status_post_production),
    @SerializedName("Released")
    RELEASED(R.string.status_released),
    @SerializedName("Canceled")
    CANCELED(R.string.status_canceled),
    @SerializedName("Returning Series")
    RETURNING_SERIES(R.string.status_returning_series),
    @SerializedName("Ended")
    ENDED(R.string.status_ended),
    @SerializedName("Pilot")
    PILOT(R.string.status_pilot),
    @SerializedName("Active")
    ACTIVE(R.string.status_active)
}