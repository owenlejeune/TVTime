package com.owenlejeune.tvtime.utils.types

import com.google.gson.annotations.SerializedName

enum class MediaViewType {
    @SerializedName("movie", alternate = ["Movie"])
    MOVIE,
    @SerializedName("tv", alternate = ["TV Show"])
    TV,
    @SerializedName("person")
    PERSON,
    EPISODE,
    MIXED,
    LIST;

    companion object {
        const val JSON_KEY = "media_type"

        operator fun get(oridinal: Int): MediaViewType {
            return values()[oridinal]
        }
    }
}

class ViewableMediaTypeException(type: MediaViewType): IllegalArgumentException("Media type given: ${type}, \n     expected one of MediaViewType.MOVIE, MediaViewType.TV")