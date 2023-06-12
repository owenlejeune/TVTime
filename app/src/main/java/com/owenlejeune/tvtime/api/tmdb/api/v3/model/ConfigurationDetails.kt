package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName

data class ConfigurationDetails(
    @SerializedName("images")
    val images: ImagesConfiguration
) {
    companion object {
        val Empty = ConfigurationDetails(ImagesConfiguration.Empty)
    }
}

data class ImagesConfiguration(
    @SerializedName("base_url")
    val baseUrl: String,
    @SerializedName("secure_base_url")
    val secureBaseUrl: String,
    @SerializedName("backdrop_sizes")
    val backdropSizes: List<String>,
    @SerializedName("logo_sizes")
    val logoSizes: List<String>,
    @SerializedName("poster_sizes")
    val posterSizes: List<String>,
    @SerializedName("profile_sizes")
    val profileSizes: List<String>,
    @SerializedName("still_sizes")
    val stillSizes: List<String>
) {
    companion object {
        val Empty = ImagesConfiguration("", "", emptyList(), emptyList(), emptyList(), emptyList(), emptyList())
    }
}
