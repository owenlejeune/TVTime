package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

class RatedMedia(
    @SerializedName("id") val id: Int,
    var type: Type
) {

    enum class Type {
        MOVIE,
        SERIES,
        EPISODE
    }

}
