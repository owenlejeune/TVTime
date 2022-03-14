package com.owenlejeune.tvtime.api.tmdb.api.v4.model

import com.google.gson.annotations.SerializedName
import com.owenlejeune.tvtime.ui.screens.MediaViewType

class UpdateListItemBody(
    @SerializedName("items") val items: List<UpdateListItemBodyItem>
)

class UpdateListItemBodyItem(
    @SerializedName("media_type") val mediaType: MediaViewType,
    @SerializedName("media_id") val id: Int,
    @SerializedName("comment") val comment: String
)
