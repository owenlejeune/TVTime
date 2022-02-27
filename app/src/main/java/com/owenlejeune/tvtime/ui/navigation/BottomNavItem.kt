package com.owenlejeune.tvtime.ui.navigation

import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.utils.ResourceUtils
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

sealed class BottomNavItem(stringRes: Int, val icon: Int, val route: String): KoinComponent {

    private val resourceUtils: ResourceUtils by inject()

    val name = resourceUtils.getString(stringRes)

    companion object {
        val Items = listOf(Movies, TV, Account, Settings)

        fun getByRoute(route: String?): BottomNavItem? {
            return when (route) {
                Movies.route -> Movies
                TV.route -> TV
                Account.route -> Account
                Favourites.route -> Favourites
                Settings.route -> Settings
                else -> null
            }
        }
    }

    object Movies: BottomNavItem(R.string.nav_movies_title, R.drawable.ic_movie, "movies_route")
    object TV: BottomNavItem(R.string.nav_tv_title, R.drawable.ic_tv, "tv_route")
    object Account: BottomNavItem(R.string.nav_account_title, R.drawable.ic_person, "account_route")
    object Favourites: BottomNavItem(R.string.nav_favourites_title, R.drawable.ic_favorite, "favourites_route")
    object Settings: BottomNavItem(R.string.nav_settings_title, R.drawable.ic_settings, "settings_route")

}
