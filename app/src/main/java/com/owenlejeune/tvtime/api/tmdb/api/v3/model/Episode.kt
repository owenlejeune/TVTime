package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Episode(
    @SerializedName("air_date") val airDate: Date?,
    @SerializedName("episode_number") val episodeNumber: Int,
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("overview") val overview: String,
    @SerializedName("production_code") val productionCode: String,
    @SerializedName("runtime") val runtime: Int,
    @SerializedName("season_number") val seasonNumber: Int,
    @SerializedName("show_id") val showId: Int,
    @SerializedName("still_path") val stillPath: String?,
    @SerializedName("vote_average") val voteAverage: Float,
    @SerializedName("vote_count") val voteCount: Int,
    @SerializedName("crew") val crew: List<EpisodeCrewMember>?,
    @SerializedName("guest_starts") val guestStars: List<EpisodeCastMember>?
)