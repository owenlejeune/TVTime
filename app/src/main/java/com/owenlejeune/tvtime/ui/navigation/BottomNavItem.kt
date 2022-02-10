package com.owenlejeune.tvtime.ui.navigation

import com.owenlejeune.tvtime.R

sealed class BottomNavItem(val name: String, val icon: Int, val route: String) {

    companion object {
        val Items = listOf(Movies, TV, Favourites, Settings)
    }

    object Movies: BottomNavItem("Movies", R.drawable.ic_movie, "movies_route")
    object TV: BottomNavItem("TV", R.drawable.ic_tv, "tv_route")
    object Favourites: BottomNavItem("Favourites", R.drawable.ic_favorite, "favourites_route")
    object Settings: BottomNavItem("Settings", R.drawable.ic_settings, "settings_route")

}
