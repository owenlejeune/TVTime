package com.owenlejeune.tvtime.ui.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.preferences.AppPreferences
import com.owenlejeune.tvtime.ui.screens.AccountViewContent
import com.owenlejeune.tvtime.ui.screens.tabs.MediaTab
import com.owenlejeune.tvtime.ui.screens.tabs.PeopleTab
import com.owenlejeune.tvtime.ui.viewmodel.HomeScreenViewModel
import com.owenlejeune.tvtime.utils.ResourceUtils
import com.owenlejeune.tvtime.utils.types.MediaViewType
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Composable
fun HomeScreenNavHost(
    appNavController: NavHostController,
    navController: NavHostController,
    startDestination: String = HomeScreenNavItem.SortedItems[0].route
) {
    val homeScreenViewModel = viewModel<HomeScreenViewModel>()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() }
    ) {
        composable(HomeScreenNavItem.Movies.route) {
            homeScreenViewModel.appBarActions.value = {}
            MediaTab(
                appNavController = appNavController,
                mediaType = MediaViewType.MOVIE,
            )
        }
        composable(HomeScreenNavItem.TV.route) {
            homeScreenViewModel.appBarActions.value = {}
            MediaTab(
                appNavController = appNavController,
                mediaType = MediaViewType.TV,
            )
        }
        composable(route = HomeScreenNavItem.Account.route) {
            AccountViewContent(appNavController = appNavController)
            homeScreenViewModel.fab.value = {}
        }
        composable(HomeScreenNavItem.People.route) {
            homeScreenViewModel.appBarActions.value = {}
            PeopleTab(
                appNavController = appNavController,
            )
        }
    }
}

sealed class HomeScreenNavItem(
    stringRes: Int,
    val icon: ImageVector,
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
        Icons.Outlined.Movie,
        "movies_route",
        { it.moviesTabPosition },
        { p, i -> p.moviesTabPosition = i }
    )
    object TV: HomeScreenNavItem(
        R.string.nav_tv_title,
        Icons.Outlined.Tv,
        "tv_route",
        { it.tvTabPosition },
        { p, i -> p.tvTabPosition = i }
    )
    object Account: HomeScreenNavItem(
        R.string.nav_account_title,
        Icons.Outlined.Person,
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
        Icons.Outlined.Face,
        "people_route",
        { it.peopleTabPosition },
        { p, i -> p.peopleTabPosition = i }
    )

}
