package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName

class AccountList(
    @SerializedName("description") val description: String,
    @SerializedName("favorite_count") val favoriteCount: Int,
    @SerializedName("id") val id: Int,
    @SerializedName("item_count") val itemCount: Int,
    @SerializedName("iso_639_1") val languageCode: String,
    @SerializedName("list_type") val listType: String, // media type
    @SerializedName("name") val name: String
)
