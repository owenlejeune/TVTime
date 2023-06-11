package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName
import com.owenlejeune.tvtime.utils.types.MediaViewType

class WatchlistBody(
    @SerializedName("media_type") val mediaType: MediaViewType,
    @SerializedName("media_id") val mediaId: Int,
    @SerializedName("watchlist") val onWatchlist: Boolean
)
