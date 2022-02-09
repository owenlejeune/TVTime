package com.owenlejeune.tvtime.ui.components

import com.owenlejeune.tvtime.R

sealed class NavItems(val name: String, val icon: Int, val route: String) {

    companion object {
        val Items = listOf(Movies, TV, Favourites, Settings)
    }

    object Movies: NavItems("Movies", R.drawable.ic_movie, "movies_route")
    object TV: NavItems("TV", R.drawable.ic_tv, "tv_route")
    object Favourites: NavItems("Favourites", R.drawable.ic_favorite, "favourites_route")
    object Settings: NavItems("Settings", R.drawable.ic_settings, "settings_route")

}
