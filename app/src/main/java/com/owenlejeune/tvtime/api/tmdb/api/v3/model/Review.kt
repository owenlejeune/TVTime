package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName
import java.util.Date

class Review(
    @SerializedName("id") val id: String,
    @SerializedName("author") val author: String,
    @SerializedName("author_details") val authorDetails: AuthorDetails,
    @SerializedName("content") val content: String,
    @SerializedName("created_at") val createdAt: Date?,
    @SerializedName("updated_at") val updatedAt: Date?
)