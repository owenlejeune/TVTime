package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

class CreateSessionResponse(
    @SerializedName("success") val isSuccess: Boolean,
    @SerializedName("session_id") val sessionId: String
)
