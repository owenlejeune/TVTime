package com.owenlejeune.tvtime.api.tmdb.api.v3.model

import com.google.gson.annotations.SerializedName

data class Genre(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
) {

    companion object {
        val placeholderData: List<Genre> by lazy {
            listOf<Genre>().toMutableList().apply {
                for (i in 0 until 3) {
                    add(Genre(i, "               "))
                }
            }
        }
    }


}
