package com.owenlejeune.tvtime.ui.navigation

import android.widget.Toast
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.owenlejeune.tvtime.ui.screens.EpisodeDetailsScreen
import com.owenlejeune.tvtime.ui.screens.GalleryView
import com.owenlejeune.tvtime.ui.screens.HomeScreen
import com.owenlejeune.tvtime.ui.screens.KeywordResultsScreen
import com.owenlejeune.tvtime.ui.screens.KnownForScreen
import com.owenlejeune.tvtime.ui.screens.ListDetailScreen
import com.owenlejeune.tvtime.ui.screens.MediaDetailScreen
import com.owenlejeune.tvtime.ui.screens.PersonDetailScreen
import com.owenlejeune.tvtime.ui.screens.SearchScreen
import com.owenlejeune.tvtime.ui.screens.SeasonDetailsScreen
import com.owenlejeune.tvtime.ui.screens.SeasonListScreen
import com.owenlejeune.tvtime.ui.screens.SettingsScreen
import com.owenlejeune.tvtime.ui.screens.WebLinkScreen
import com.owenlejeune.tvtime.ui.viewmodel.ApplicationViewModel
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
    val applicationViewModel = viewModel<ApplicationViewModel>()

    NavHost(
        navController = appNavController,
        startDestination = startDestination,
        enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(500)) },
        popEnterTransition = { fadeIn(tween(500)) },
        exitTransition = { fadeOut(tween(500)) },
        popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(500)) }
    ) {
        // About View
        composable(route = AppNavItem.AboutView.route) {
            applicationViewModel.currentRoute.value = AppNavItem.AboutView.route
            AboutScreen(appNavController = appNavController)
        }
        // Account View
        composable(
            route = AppNavItem.AccountView.route,
            deepLinks = listOf(
                navDeepLink { uriPattern = "app://tvtime.auth.{${NavConstants.ACCOUNT_KEY}}" }
            )
        ) {
            val deepLink = it.arguments?.getString(NavConstants.ACCOUNT_KEY)
            applicationViewModel.currentRoute.value = AppNavItem.AccountView.route
            AccountScreen(
                appNavController = appNavController,
                doSignInPartTwo = deepLink == NavConstants.AUTH_REDIRECT_PAGE
            )
        }
        // Cast Crew List View
        composable(
            route = AppNavItem.CastCrewListView.route.plus("/{${NavConstants.TYPE_KEY}}/{${NavConstants.ID_KEY}}"),
            arguments = listOf(
                navArgument(NavConstants.TYPE_KEY) { type = NavType.EnumType(MediaViewType::class.java) },
                navArgument(NavConstants.ID_KEY) { type = NavType.IntType }
            )
        ) { navBackStackEntry ->
            val type = navBackStackEntry.arguments?.safeGetSerializable(NavConstants.TYPE_KEY, MediaViewType::class.java)!!
            val id = navBackStackEntry.arguments?.getInt(NavConstants.ID_KEY)!!

            applicationViewModel.currentRoute.value = AppNavItem.CastCrewListView.withArgs(type, id)
            CastCrewListScreen(appNavController = appNavController, type = type, id = id)
        }
        // Detail View
        composable(
            route = AppNavItem.DetailView.route.plus("/{${NavConstants.TYPE_KEY}}/{${NavConstants.ID_KEY}}"),
            arguments = listOf(
                navArgument(NavConstants.ID_KEY) { type = NavType.IntType },
                navArgument(NavConstants.TYPE_KEY) { type = NavType.EnumType(MediaViewType::class.java) }
            )
        ) { navBackStackEntry ->
            val mediaType = navBackStackEntry.arguments?.safeGetSerializable(NavConstants.TYPE_KEY, MediaViewType::class.java)!!
            val id = navBackStackEntry.arguments?.getInt(NavConstants.ID_KEY)!!

            applicationViewModel.currentRoute.value = AppNavItem.DetailView.withArgs(mediaType, id)
            when (mediaType) {
                MediaViewType.PERSON -> {
                    PersonDetailScreen(
                        appNavController = appNavController,
                        personId = id
                    )
                }
                MediaViewType.LIST -> {
                    ListDetailScreen(
                        appNavController = appNavController,
                        itemId = id
                    )
                }
                MediaViewType.MOVIE, MediaViewType.TV -> {
                    MediaDetailScreen(
                        appNavController = appNavController,
                        itemId = id,
                        type = mediaType,
                        windowSize = windowSize
                    )
                }
                MediaViewType.SEASON -> {
                    SeasonDetailsScreen(
                        appNavController = appNavController,
                        codedId = id
                    )
                }
                MediaViewType.EPISODE -> {
                    EpisodeDetailsScreen(
                        appNavController = appNavController,
                        codedId = id
                    )
                }
                else -> {
                    appNavController.popBackStack()
                    Toast.makeText(LocalContext.current, stringResource(R.string.unexpected_error), Toast.LENGTH_SHORT).show()
                }
            }
        }
        // Gallery View
        composable(
            route = AppNavItem.GalleryView.route.plus("/{${NavConstants.TYPE_KEY}}/{${NavConstants.ID_KEY}}"),
            arguments = listOf(
                navArgument(NavConstants.TYPE_KEY) { type = NavType.EnumType(MediaViewType::class.java) },
                navArgument(NavConstants.ID_KEY) { type = NavType.IntType }
            )
        ) { navBackStackEntry ->
            val type = navBackStackEntry.arguments?.safeGetSerializable(NavConstants.TYPE_KEY, MediaViewType::class.java)!!
            val id = navBackStackEntry.arguments?.getInt(NavConstants.ID_KEY)!!

            applicationViewModel.currentRoute.value = AppNavItem.GalleryView.withArgs(type, id)
            GalleryView(id = id, type = type, appNavController = appNavController)
        }
        // Keywords View
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

            applicationViewModel.currentRoute.value = AppNavItem.KeywordsView.withArgs(type, keywords, id)
            KeywordResultsScreen(
                type = type,
                keyword = keywords,
                id = id,
                appNavController = appNavController
            )
        }
        // Known For View
        composable(
            route = AppNavItem.KnownForView.route.plus("/{${NavConstants.ID_KEY}}"),
            arguments = listOf(
                navArgument(NavConstants.ID_KEY) { type = NavType.IntType }
            )
        ) { navBackStackEntry ->
            val id = navBackStackEntry.arguments?.getInt(NavConstants.ID_KEY)!!

            applicationViewModel.currentRoute.value = AppNavItem.KnownForView.withArgs(id)
            KnownForScreen(appNavController = appNavController, id = id)
        }
        // Main View
        composable(route = AppNavItem.MainView.route) {
            applicationViewModel.currentRoute.value = AppNavItem.MainView.route
            HomeScreen(
                appNavController = appNavController,
                mainNavStartRoute = mainNavStartRoute,
                windowSize = windowSize
            )
        }
        // Search View
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

                applicationViewModel.currentRoute.value = AppNavItem.SearchView.withArgs(type, title)
                SearchScreen(
                    appNavController = appNavController,
                    title = title,
                    mediaViewType = type
                )
            }
        }
        // Season List View
        composable(
            route = AppNavItem.SeasonListView.route.plus("/{${NavConstants.ID_KEY}}"),
            arguments = listOf(
                navArgument(NavConstants.ID_KEY) { type = NavType.IntType }
            )
        ) { navBackStackEntry ->
            val id = navBackStackEntry.arguments?.getInt(NavConstants.ID_KEY)!!

            applicationViewModel.currentRoute.value = AppNavItem.SeasonListView.withArgs(id)
            SeasonListScreen(id = id, appNavController = appNavController)
        }
        // Settings View
        composable(AppNavItem.SettingsView.route) {
            applicationViewModel.currentRoute.value = AppNavItem.SettingsView.route
            SettingsScreen(appNavController = appNavController, windowSizeClass = windowSize)
        }
        // Web Link View
        composable(
            route = AppNavItem.WebLinkView.route.plus("/{${NavConstants.WEB_LINK_KEY}}"),
            arguments = listOf(
                navArgument(NavConstants.WEB_LINK_KEY) { type = NavType.StringType }
            )
        ) {
            val url = it.arguments?.getString(NavConstants.WEB_LINK_KEY)
            url?.let {
                applicationViewModel.currentRoute.value = AppNavItem.WebLinkView.withArgs(url)
                WebLinkScreen(url = url, appNavController = appNavController)
            }
        }
    }
}

sealed class AppNavItem(val route: String) {

    companion object {
        val Items = listOf(MainView, DetailView, SettingsView)
    }

    object AboutView: AppNavItem("about_route")
    object AccountView: AppNavItem("account_route")
    object CastCrewListView: AppNavItem("cast_crew_list_route") {
        fun withArgs(type: MediaViewType, id: Int) = route.plus("/$type/$id")
    }
    object DetailView: AppNavItem("detail_route") {
        fun withArgs(type: MediaViewType, id: Int) = route.plus("/${type}/${id}")
    }
    object GalleryView: AppNavItem("gallery_view_route") {
        fun withArgs(type: MediaViewType, id: Int) = route.plus("/$type/$id")
    }
    object KeywordsView: AppNavItem("keywords_route") {
        fun withArgs(type: MediaViewType, keyword: String, id: Int) = route.plus("/$type?keyword=$keyword&keywordId=$id")
    }
    object KnownForView: AppNavItem("known_for_route") {
        fun withArgs(id: Int) = route.plus("/$id")
    }
    object MainView: AppNavItem("main_route")
    object SearchView: AppNavItem("search_route") {
        fun withArgs(searchType: MediaViewType, pageTitle: String) = route.plus("?searchType=$searchType&pageTitle=$pageTitle")
    }
    object SeasonListView: AppNavItem("season_list_route") {
        fun withArgs(id: Int) = route.plus("/$id")
    }
    object SettingsView: AppNavItem("settings_route")
    object WebLinkView: AppNavItem("web_link_route") {
        fun withArgs(url: String) = route.plus("/$url")
    }

}
