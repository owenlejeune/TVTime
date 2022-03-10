package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

class WatchlistBody(
    @SerializedName("media_type") val mediaType: String, // media type
    @SerializedName("media_id") val mediaId: Int,
    @SerializedName("watchlist") val onWatchlist: Boolean
)
