package com.owenlejeune.tvtime.utils.types

import com.google.gson.annotations.SerializedName

enum class Gender(val rawValue: Int) {
    @SerializedName("1")
    MALE(1),
    @SerializedName("2")
    FEMALE(2),
    UNDEFINED(-1)
}