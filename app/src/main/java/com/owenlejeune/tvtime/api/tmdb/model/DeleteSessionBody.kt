package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

class DeleteSessionBody(
    @SerializedName("session_id") val sessionsId: String
)
