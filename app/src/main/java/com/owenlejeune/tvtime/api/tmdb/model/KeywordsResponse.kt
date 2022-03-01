package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

class KeywordsResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("keywords") val keywords: List<Keyword>?
)