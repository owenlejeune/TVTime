package com.owenlejeune.tvtime.utils.types

import com.google.gson.annotations.SerializedName

enum class TimeWindow {
    @SerializedName("day")
    DAY,
    @SerializedName("week")
    WEEK
}