package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName

data class ConfigurationJob(
    @SerializedName("department")
    val department: String,
    @SerializedName("jobs")
    val jobs: List<String>
)