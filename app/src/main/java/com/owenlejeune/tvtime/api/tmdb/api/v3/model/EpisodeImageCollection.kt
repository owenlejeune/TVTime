package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName

data class EpisodeImageCollection(
    @SerializedName("id") val id: Int,
    @SerializedName("stills") val stills: List<EpisodeStill>
)

data class EpisodeStill(
    @SerializedName("aspect_ratio") val aspectRation: Float,
    @SerializedName("height") val height: Int,
    @SerializedName("iso_639_1") val language: String?,
    @SerializedName("file_path") val stillPath: String,
    @SerializedName("vote_average") val voteAverage: Float,
    @SerializedName("vote_count") val voteCount: Int,
    @SerializedName("width") val width: Int
)