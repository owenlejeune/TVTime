package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

class AccountListResponse(
    @SerializedName("page") val page: Int,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int,
    @SerializedName("results") val results: List<AccountList>
)
