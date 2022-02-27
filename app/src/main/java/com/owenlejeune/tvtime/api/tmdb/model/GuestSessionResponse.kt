package com.owenlejeune.tvtime.api.tmdb.model

import com.google.gson.annotations.SerializedName

class GuestSessionResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("guest_session_id") val guestSessionId: String,
    @SerializedName("expires_at") val expiry: String
)
