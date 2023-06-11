package com.owenlejeune.tvtime.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.extensions.WindowSizeClass
import com.owenlejeune.tvtime.preferences.AppPreferences
import com.owenlejeune.tvtime.ui.screens.SearchScreen
import com.owenlejeune.tvtime.ui.screens.AboutScreen
import com.owenlejeune.tvtime.ui.screens.AccountScreen
import com.owenlejeune.tvtime.ui.screens.ListDetailScreen
import com.owenlejeune.tvtime.ui.screens.MediaDetailScreen
import com.owenlejeune.tvtime.utils.types.MediaViewType
import com.owenlejeune.tvtime.ui.screens.PersonDetailScreen
import com.owenlejeune.tvtime.ui.screens.SettingsScreen
import com.owenlejeune.tvtime.ui.screens.WebLinkScreen
import com.owenlejeune.tvtime.ui.screens.HomeScreen
import com.owenlejeune.tvtime.utils.NavConstants
import org.koin.java.KoinJavaComponent

@Composable
fun AppNavigationHost(
    startDestination: String = AppNavItem.MainView.route,
    mainNavStartRoute: String = AppNavItem.Items[0].route,
    appNavController: NavHostController,
    windowSize: WindowSizeClass,
    preferences: AppPreferences = KoinJavaComponent.get(AppPreferences::class.java)
) {
    NavHost(navController = appNavController, startDestination = startDestination) {
        composable(AppNavItem.MainView.route) {
            HomeScreen(
                appNavController = appNavController,
                mainNavStartRoute = mainNavStartRoute,
                windowSize = windowSize
            )
        }
        composable(
            AppNavItem.DetailView.route.plus("/{${NavConstants.TYPE_KEY}}/{${NavConstants.ID_KEY}}"),
            arguments = listOf(
                navArgument(NavConstants.ID_KEY) { type = NavType.IntType },
                navArgument(NavConstants.TYPE_KEY) { type = NavType.EnumType(MediaViewType::class.java) }
            )
        ) { navBackStackEntry ->
            val args = navBackStackEntry.arguments
            val mediaType = args?.getSerializable(NavConstants.TYPE_KEY) as MediaViewType

            when (mediaType) {
                MediaViewType.PERSON -> {
                    PersonDetailScreen(
                        appNavController = appNavController,
                        personId = args.getInt(NavConstants.ID_KEY)
                    )
                }
                MediaViewType.LIST -> {
                    ListDetailScreen(
                        appNavController = appNavController,
                        itemId = args.getInt(NavConstants.ID_KEY),
                        windowSize = windowSize
                    )
                }
                else -> {
                    MediaDetailScreen(
                        appNavController = appNavController,
                        itemId = args.getInt(NavConstants.ID_KEY),
                        type = mediaType,
                        windowSize = windowSize
                    )
                }
            }
        }
        composable(
            AppNavItem.SettingsView.route.plus("/{${NavConstants.SETTINGS_KEY}}"),
            arguments = listOf(
                navArgument(NavConstants.SETTINGS_KEY) { type = NavType.StringType }
            )
        ) {
            val route = it.arguments?.getString(NavConstants.SETTINGS_KEY)
            SettingsScreen(
                appNavController = appNavController,
                route = route
            )
        }
        composable(AppNavItem.SettingsView.route) {
            SettingsScreen(appNavController = appNavController)
        }
        composable(
            route = AppNavItem.SearchView.route.plus("?searchType={${NavConstants.SEARCH_ID_KEY}}&pageTitle={${NavConstants.SEARCH_TITLE_KEY}}"),
            arguments = listOf(
                navArgument(NavConstants.SEARCH_ID_KEY) { type = NavType.EnumType(MediaViewType::class.java) },
                navArgument(NavConstants.SEARCH_TITLE_KEY) { type = NavType.StringType }
            )
        ) {
            it.arguments?.let { arguments ->
                val (type, title) = if (preferences.multiSearch) {
                    Pair(
                        MediaViewType.MIXED,
                        stringResource(id = R.string.search_all_title)
                    )
                } else {
                    Pair(
                        arguments.getSerializable(NavConstants.SEARCH_ID_KEY) as MediaViewType,
                        arguments.getString(NavConstants.SEARCH_TITLE_KEY) ?: ""
                    )
                }

                SearchScreen(
                    appNavController = appNavController,
                    title = title,
                    mediaViewType = type
                )
            }
        }
        composable(
            route = AppNavItem.WebLinkView.route.plus("/{${NavConstants.WEB_LINK_KEY}}"),
            arguments = listOf(
                navArgument(NavConstants.WEB_LINK_KEY) { type = NavType.StringType }
            )
        ) {
            val url = it.arguments?.getString(NavConstants.WEB_LINK_KEY)
            url?.let {
                WebLinkScreen(url = url, appNavController = appNavController)
            }
        }
        composable(
            route = AppNavItem.AccountView.route,
            deepLinks = listOf(
                navDeepLink { uriPattern = "app://tvtime.auth.{${NavConstants.ACCOUNT_KEY}}" }
            )
        ) {
            val deepLink = it.arguments?.getString(NavConstants.ACCOUNT_KEY)
            AccountScreen(
                appNavController = appNavController,
                doSignInPartTwo = deepLink == NavConstants.AUTH_REDIRECT_PAGE
            )
        }
        composable(route = AppNavItem.AboutView.route) {
            AboutScreen(appNavController = appNavController)
        }
    }
}

sealed class AppNavItem(val route: String) {

    companion object {
        val Items = listOf(MainView, DetailView, SettingsView)
    }

    object MainView: AppNavItem("main_route")
    object DetailView: AppNavItem("detail_route") {
        fun withArgs(type: MediaViewType, id: Int) = route.plus("/${type}/${id}")
    }
    object SettingsView: AppNavItem("settings_route") {
        fun withArgs(page: String) = route.plus("/$page")
    }
    object SearchView: AppNavItem("search_route") {
        fun withArgs(searchType: MediaViewType, pageTitle: String) = route.plus("?searchType=$searchType&pageTitle=$pageTitle")
    }
    object WebLinkView: AppNavItem("web_link_route") {
        fun withArgs(url: String) = route.plus("/$url")
    }
    object AccountView: AppNavItem("account_route")
    object AboutView: AppNavItem("about_route")

}
