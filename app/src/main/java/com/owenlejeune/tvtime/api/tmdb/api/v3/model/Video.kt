package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName
import com.owenlejeune.tvtime.R

class Video(
    @SerializedName("iso_639_1") val language: String,
    @SerializedName("iso_3166_1") val region: String,
    @SerializedName("name") val name: String,
    @SerializedName("key") val key: String,
    @SerializedName("site") val site: String,
    @SerializedName("size") val size: Int,
    @SerializedName("type") val type: Type,
    @SerializedName("official") val isOfficial: Boolean
) {
    enum class Type(val stringRes: Int) {
        @SerializedName("Clip")
        CLIP(R.string.video_type_clip),
        @SerializedName("Behind the Scenes")
        BEHIND_THE_SCENES(R.string.video_type_behind_the_scenes),
        @SerializedName("Featurette")
        FEATURETTE(R.string.video_type_featureette),
        @SerializedName("Teaser")
        TEASER(R.string.video_type_teaser),
        @SerializedName("Trailer")
        TRAILER(R.string.video_type_trailer)
    }
}