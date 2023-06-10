package com.owenlejeune.tvtime.api.tmdb.api.v4.model

import com.google.gson.annotations.SerializedName

class AccountList(
    @SerializedName("iso_639_1") val languageCode: String,
    @SerializedName("id") val id: Int,
    @SerializedName("featured") val featured: Int,
    @SerializedName("description") val description: String,
    @SerializedName("revenue") val revenue: String,
    @SerializedName("public") val public: Int,
    @SerializedName("name") val name: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("created_at") val createdAt: String,
//    @SerializedName("sort_by")
    @SerializedName("backdrop_path") val backdropPath: String,
    @SerializedName("runtime") val runtime: Int,
    @SerializedName("average_rating") val averageRating: Float,
    @SerializedName("iso_3166_1") val countryCode: String,
    @SerializedName("adult") val adult: Int,
    @SerializedName("number_of_items") val numberOfItems: Int,
    @SerializedName("poster_path") val posterPath: String
)
