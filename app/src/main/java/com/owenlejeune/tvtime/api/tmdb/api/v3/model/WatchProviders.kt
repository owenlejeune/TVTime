package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName

data class WatchProviderResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("results")
    val results: Map<String, WatchProviders>
)

data class WatchProviders(
    @SerializedName("link")
    val link: String,
    @SerializedName("flatrate")
    val flaterate: List<WatchProviderDetails>?,
    @SerializedName("rent")
    val rent: List<WatchProviderDetails>?,
    @SerializedName("buy")
    val buy: List<WatchProviderDetails>?
)

data class WatchProviderDetails(
    @SerializedName("logo_path")
    val logoPath: String,
    @SerializedName("provider_id")
    val providerId: Int,
    @SerializedName("provider_name")
    val providerName: String,
    @SerializedName("display_priority")
    val displayPriority: Int
)