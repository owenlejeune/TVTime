package com.owenlejeune.tvtime.api.tmdb.api.v4.model

import com.google.gson.annotations.SerializedName
import com.owenlejeune.tvtime.utils.types.MediaViewType

class ListItemStatusResponse(
    statusMessage: String,
    isSuccess: Boolean,
    statusCode: Int,
    @SerializedName("media_type") val mediaType: MediaViewType,
    @SerializedName("id") val id: Int,
    @SerializedName("media_id") val mediaId: Int
): StatusResponse(statusMessage, isSuccess, statusCode)