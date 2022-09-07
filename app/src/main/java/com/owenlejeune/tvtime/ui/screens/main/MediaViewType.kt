package com.owenlejeune.tvtime.ui.screens.main

import com.google.gson.annotations.SerializedName

enum class MediaViewType {
    @SerializedName("movie")
    MOVIE,
    @SerializedName("tv")
    TV,
    @SerializedName("person")
    PERSON,
    EPISODE,
    MIXED;

    companion object {
        const val JSON_KEY = "media_type"

        operator fun get(oridinal: Int): MediaViewType {
            return values()[oridinal]
        }
    }
}