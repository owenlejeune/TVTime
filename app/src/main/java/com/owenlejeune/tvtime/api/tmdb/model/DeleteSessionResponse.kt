package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

class DeleteSessionResponse(
    @SerializedName("success") val success: Boolean
)