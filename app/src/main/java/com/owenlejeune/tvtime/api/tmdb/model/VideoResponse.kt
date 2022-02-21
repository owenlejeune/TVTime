package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

class VideoResponse(
    @SerializedName("results") val results: List<Video>
)