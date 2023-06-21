package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName

class AccountStates(
    @SerializedName("id")
    val id: Int,
    @SerializedName("favorite")
    val isFavorite: Boolean,
    @SerializedName("watchlist")
    val isWatchListed: Boolean,
    val isRated: Boolean,
    val rating: Int
)
