package com.owenlejeune.tvtime.api.tmdb.api.v4.model

import com.google.gson.annotations.SerializedName
import com.owenlejeune.tvtime.R

class MediaList(
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("id") val id: Int,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("total_results") val totalResults: Int,
    @SerializedName("public") val isPublic: Boolean,
    @SerializedName("results") val results: List<ListItem>,
    @SerializedName("iso_639_1") val language: String,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("description") val description: String,
    @SerializedName("created_by") val createdBy: ListAuthor,
    @SerializedName("iso_3166_1") val localeCode: String,
    @SerializedName("average_rating") val averageRating: Float,
    @SerializedName("runtime") val runtime: Int,
    @SerializedName("name") val name: String,
    @SerializedName("revenue") val revenue: Long,
    @SerializedName("sort_by") val sortBy: SortOrder
)

enum class SortOrder(
    val stringKey: Int,
    val sort: (List<ListItem>) -> List<ListItem>
) {
    @SerializedName("original_order.asc")
    ORIGINAL_ASCENDING(
        R.string.sort_order_original_ascending,
        { it }
    ),
    @SerializedName("original_order.desc")
    ORIGINAL_DESCENDING(
        R.string.sort_order_original_descending,
        { ORIGINAL_ASCENDING.sort(it).reversed() }
    ),
    @SerializedName("vote_average.asc")
    RATING_ASCENDING(
        R.string.sort_order_rating_ascending,
        { lin -> lin.sortedBy { it.voteAverage } }
    ),
    @SerializedName("vote_average.desc")
    RATING_DESCENDING(
        R.string.sort_order_rating_descending,
        { lin -> RATING_ASCENDING.sort(lin).reversed() }
    ),
    @SerializedName("primary_release_date.asc")
    RELEASE_DATE_ASCENDING(
        R.string.sort_order_release_date_ascending,
        { lin -> lin.sortedBy { it.releaseDate } }
    ),
    @SerializedName("primary_release_date.desc")
    RELEASE_DATE_DESCENDING(
        R.string.sort_order_release_date_descending,
        { lin -> RELEASE_DATE_ASCENDING.sort(lin).reversed() }
    ),
    @SerializedName("title.asc")
    TITLE_ASCENDING(
        R.string.sort_order_title_ascending,
        { lin -> lin.sortedBy { it.title } }
    ),
    @SerializedName("title.desc")
    TITLE_DESCENDING(
        R.string.sort_order_title_descending,
        { lin -> TITLE_ASCENDING.sort(lin).reversed() }
    )
}