package com.owenlejeune.tvtime.ui.navigation

import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.preferences.AppPreferences
import com.owenlejeune.tvtime.utils.ResourceUtils
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

sealed class BottomNavItem(
    stringRes: Int,
    val icon: Int,
    val route: String,
    private val orderGetter: (AppPreferences) -> Int,
    private val orderSetter: (AppPreferences, Int) -> Unit
): KoinComponent {

    private val appPreferences: AppPreferences by inject()
    private val resourceUtils: ResourceUtils by inject()

    val name = resourceUtils.getString(stringRes)
    var order: Int
        get() = orderGetter.invoke(appPreferences)
        set(value) { orderSetter.invoke(appPreferences, value) }

    companion object {
        val SortedItems
            get() = Items.filter { it.order > -1 }.sortedBy { it.order }.ifEmpty { Items }

        val Items by lazy {
            listOf(Movies, TV, People, Account)
        }

        fun getByRoute(route: String?): BottomNavItem? {
            return when (route) {
                Movies.route -> Movies
                TV.route -> TV
                Account.route -> Account
                else -> null
            }
        }
    }

    object Movies: BottomNavItem(R.string.nav_movies_title, R.drawable.ic_movie, "movies_route", { it.moviesTabPosition }, { p, i -> p.moviesTabPosition = i } )
    object TV: BottomNavItem(R.string.nav_tv_title, R.drawable.ic_tv, "tv_route", { it.tvTabPosition }, { p, i -> p.tvTabPosition = i } )
    object Account: BottomNavItem(R.string.nav_account_title, R.drawable.ic_person, "account_route", { it.accountTabPosition }, { p, i -> p.accountTabPosition = i } )
    object People: BottomNavItem(R.string.nav_people_title, R.drawable.ic_face, "people_route", { it.peopleTabPosition }, { p, i -> p.peopleTabPosition = i } )

}
