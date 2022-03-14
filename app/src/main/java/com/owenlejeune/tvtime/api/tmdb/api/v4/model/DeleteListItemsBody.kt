package com.owenlejeune.tvtime.api.tmdb.api.v4.model

import com.google.gson.annotations.SerializedName
import com.owenlejeune.tvtime.ui.screens.MediaViewType

class DeleteListItemsBody(
    @SerializedName("media_id") val id: Int,
    @SerializedName("media_type") val mediaType: MediaViewType
)
