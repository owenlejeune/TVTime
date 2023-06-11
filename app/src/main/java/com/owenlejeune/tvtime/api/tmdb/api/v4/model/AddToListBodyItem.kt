package com.owenlejeune.tvtime.api.tmdb.api.v4.model

import com.google.gson.annotations.SerializedName
import com.owenlejeune.tvtime.utils.types.MediaViewType

class AddToListBody(
    @SerializedName("items") val items: List<AddToListBodyItem>
)

class AddToListBodyItem(
    @SerializedName("media_type") val mediaType: MediaViewType,
    @SerializedName("media_id") val id: Int
)
