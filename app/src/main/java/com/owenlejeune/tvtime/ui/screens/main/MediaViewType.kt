package com.owenlejeune.tvtime.ui.screens.main

import com.google.gson.annotations.SerializedName

enum class MediaViewType {
    @SerializedName("movie")
    MOVIE,
    @SerializedName("tv")
    TV,
    PERSON,
    EPISODE
}