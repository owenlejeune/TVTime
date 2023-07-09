package com.owenlejeune.tvtime.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import com.owenlejeune.tvtime.extensions.safeGetSerializable
import com.owenlejeune.tvtime.preferences.AppPreferences
import com.owenlejeune.tvtime.ui.screens.AboutScreen
import com.owenlejeune.tvtime.ui.screens.AccountScreen
import com.owenlejeune.tvtime.ui.screens.CastCrewListScreen
import com.owenlejeune.tvtime.ui.screens.GalleryView
import com.owenlejeune.tvtime.ui.screens.HomeScreen
import com.owenlejeune.tvtime.ui.screens.KeywordResultsScreen
import com.owenlejeune.tvtime.ui.screens.KnownForScreen
import com.owenlejeune.tvtime.ui.screens.ListDetailScreen
import com.owenlejeune.tvtime.ui.screens.MediaDetailScreen
import com.owenlejeune.tvtime.ui.screens.PersonDetailScreen
import com.owenlejeune.tvtime.ui.screens.SearchScreen
import com.owenlejeune.tvtime.ui.screens.SeasonListScreen
import com.owenlejeune.tvtime.ui.screens.SettingsScreen
import com.owenlejeune.tvtime.ui.screens.WebLinkScreen
import com.owenlejeune.tvtime.utils.NavConstants
import com.owenlejeune.tvtime.utils.types.MediaViewType
import org.koin.java.KoinJavaComponent

@Composable
fun AppNavigationHost(
    startDestination: String = AppNavItem.MainView.route,
    mainNavStartRoute: String = AppNavItem.Items[0].route,
    appNavController: NavHostController,
    windowSize: WindowSizeClass,
    preferences: AppPreferences = KoinJavaComponent.get(AppPreferences::class.java)
) {
    NavHost(
        navController = appNavController,
        startDestination = startDestination,
        enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(500)) },
        popEnterTransition = { fadeIn(tween(500)) },
        exitTransition = { fadeOut(tween(500)) },
        popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(500)) }
    ) {
        composable(route = AppNavItem.MainView.route) {
            HomeScreen(
                appNavController = appNavController,
                mainNavStartRoute = mainNavStartRoute,
                windowSize = windowSize
            )
        }
        composable(
            route = AppNavItem.DetailView.route.plus("/{${NavConstants.TYPE_KEY}}/{${NavConstants.ID_KEY}}"),
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
                        arguments.safeGetSerializable(NavConstants.SEARCH_ID_KEY, MediaViewType::class.java)!!,
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
        composable(
            route = AppNavItem.KeywordsView.route.plus("/{${NavConstants.KEYWORD_TYPE_KEY}}?keyword={${NavConstants.KEYWORD_NAME_KEY}}&keywordId={${NavConstants.KEYWORD_ID_KEY}}"),
            arguments = listOf(
                navArgument(NavConstants.KEYWORD_TYPE_KEY) { type = NavType.EnumType(MediaViewType::class.java) },
                navArgument(NavConstants.KEYWORD_NAME_KEY) { type = NavType.StringType },
                navArgument(NavConstants.KEYWORD_ID_KEY) { type = NavType.IntType }
            )
        ) { navBackStackEntry ->
            val type = navBackStackEntry.arguments?.safeGetSerializable(NavConstants.KEYWORD_TYPE_KEY, MediaViewType::class.java)!!
            val keywords = navBackStackEntry.arguments?.getString(NavConstants.KEYWORD_NAME_KEY) ?: ""
            val id = navBackStackEntry.arguments?.getInt(NavConstants.KEYWORD_ID_KEY)!!

            KeywordResultsScreen(
                type = type,
                keyword = keywords,
                id = id,
                appNavController = appNavController
            )
        }
        composable(
            route = AppNavItem.KnownForView.route.plus("/{${NavConstants.ID_KEY}}"),
            arguments = listOf(
                navArgument(NavConstants.ID_KEY) { type = NavType.IntType }
            )
        ) { navBackStackEntry ->
            val id = navBackStackEntry.arguments?.getInt(NavConstants.ID_KEY)!!

            KnownForScreen(appNavController = appNavController, id = id)
        }
        composable(
            route = AppNavItem.GalleryView.route.plus("/{${NavConstants.TYPE_KEY}}/{${NavConstants.ID_KEY}}"),
            arguments = listOf(
                navArgument(NavConstants.TYPE_KEY) { type = NavType.EnumType(MediaViewType::class.java) },
                navArgument(NavConstants.ID_KEY) { type = NavType.IntType }
            )
        ) { navBackStackEntry ->
            val type = navBackStackEntry.arguments?.safeGetSerializable(NavConstants.TYPE_KEY, MediaViewType::class.java)!!
            val id = navBackStackEntry.arguments?.getInt(NavConstants.ID_KEY)!!

            GalleryView(id = id, type = type, appNavController = appNavController)
        }
        composable(
            route = AppNavItem.SeasonListView.route.plus("/{${NavConstants.ID_KEY}}"),
            arguments = listOf(
                navArgument(NavConstants.ID_KEY) { type = NavType.IntType }
            )
        ) { navBackStackEntry ->
            val id = navBackStackEntry.arguments?.getInt(NavConstants.ID_KEY)!!

            SeasonListScreen(id = id, appNavController = appNavController)
        }
        composable(
            route = AppNavItem.CaseCrewListView.route.plus("/{${NavConstants.TYPE_KEY}}/{${NavConstants.ID_KEY}}"),
            arguments = listOf(
                navArgument(NavConstants.TYPE_KEY) { type = NavType.EnumType(MediaViewType::class.java) },
                navArgument(NavConstants.ID_KEY) { type = NavType.IntType }
            )
        ) { navBackStackEntry ->
            val type = navBackStackEntry.arguments?.safeGetSerializable(NavConstants.TYPE_KEY, MediaViewType::class.java)!!
            val id = navBackStackEntry.arguments?.getInt(NavConstants.ID_KEY)!!

            CastCrewListScreen(appNavController = appNavController, type = type, id = id)
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
    object SettingsView: AppNavItem("settings_route")
    object SearchView: AppNavItem("search_route") {
        fun withArgs(searchType: MediaViewType, pageTitle: String) = route.plus("?searchType=$searchType&pageTitle=$pageTitle")
    }
    object WebLinkView: AppNavItem("web_link_route") {
        fun withArgs(url: String) = route.plus("/$url")
    }
    object AccountView: AppNavItem("account_route")
    object AboutView: AppNavItem("about_route")
    object KeywordsView: AppNavItem("keywords_route") {
        fun withArgs(type: MediaViewType, keyword: String, id: Int) = route.plus("/$type?keyword=$keyword&keywordId=$id")
    }
    object KnownForView: AppNavItem("known_for_route") {
        fun withArgs(id: Int) = route.plus("/$id")
    }
    object GalleryView: AppNavItem("gallery_view_route") {
        fun withArgs(type: MediaViewType, id: Int) = route.plus("/$type/$id")
    }
    object SeasonListView: AppNavItem("season_list_route") {
        fun withArgs(id: Int) = route.plus("/$id")
    }
    object CaseCrewListView: AppNavItem("cast_crew_list_route") {
        fun withArgs(type: MediaViewType, id: Int) = route.plus("/$type/$id")
    }

}
