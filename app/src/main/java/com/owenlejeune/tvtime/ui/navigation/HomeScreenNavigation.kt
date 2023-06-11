package com.owenlejeune.tvtime.ui.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.preferences.AppPreferences
import com.owenlejeune.tvtime.ui.screens.AccountViewContent
import com.owenlejeune.tvtime.ui.screens.tabs.MediaTab
import com.owenlejeune.tvtime.utils.types.MediaViewType
import com.owenlejeune.tvtime.ui.screens.tabs.PeopleTab
import com.owenlejeune.tvtime.utils.ResourceUtils
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Composable
fun HomeScreenNavHost(
    appNavController: NavHostController,
    navController: NavHostController,
    fab: MutableState<@Composable () -> Unit>,
    appBarTitle: MutableState<@Composable () -> Unit>,
    appBarActions: MutableState<@Composable (RowScope.() -> Unit)> = mutableStateOf({}),
    startDestination: String = HomeScreenNavItem.SortedItems[0].route
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(HomeScreenNavItem.Movies.route) {
            appBarActions.value = {}
            MediaTab(
                appBarTitle = appBarTitle,
                appNavController = appNavController,
                mediaType = MediaViewType.MOVIE,
                fab = fab
            )
        }
        composable(HomeScreenNavItem.TV.route) {
            appBarActions.value = {}
            MediaTab(
                appBarTitle = appBarTitle,
                appNavController = appNavController,
                mediaType = MediaViewType.TV,
                fab = fab
            )
        }
        composable(route = HomeScreenNavItem.Account.route) {
            AccountViewContent(appNavController = appNavController)
            fab.value = {}
        }
        composable(HomeScreenNavItem.People.route) {
            appBarActions.value = {}
            PeopleTab(
                appBarTitle = appBarTitle,
                appNavController = appNavController,
                fab = fab
            )
        }
    }
}

sealed class HomeScreenNavItem(
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

        fun getByRoute(route: String?): HomeScreenNavItem? {
            return when (route) {
                Movies.route -> Movies
                TV.route -> TV
                Account.route -> Account
                else -> null
            }
        }
    }

    object Movies: HomeScreenNavItem(
        R.string.nav_movies_title,
        R.drawable.ic_movie,
        "movies_route",
        { it.moviesTabPosition },
        { p, i -> p.moviesTabPosition = i }
    )
    object TV: HomeScreenNavItem(
        R.string.nav_tv_title,
        R.drawable.ic_tv,
        "tv_route",
        { it.tvTabPosition },
        { p, i -> p.tvTabPosition = i }
    )
    object Account: HomeScreenNavItem(
        R.string.nav_account_title,
        R.drawable.ic_person,
        "account_route",
        {
//            if (SessionManager.currentSession.value?.isAuthorized == true) {
//                it.accountTabPosition
//            } else {
            -2
//            }
        },
        { p, i -> p.accountTabPosition = i }
    )
    object People: HomeScreenNavItem(
        R.string.nav_people_title,
        R.drawable.ic_face,
        "people_route",
        { it.peopleTabPosition },
        { p, i -> p.peopleTabPosition = i }
    )

}
