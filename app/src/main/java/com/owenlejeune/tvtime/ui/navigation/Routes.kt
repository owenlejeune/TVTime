package com.owenlejeune.tvtime.ui.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.owenlejeune.tvtime.ui.screens.main.MediaDetailView
import com.owenlejeune.tvtime.ui.screens.main.MediaViewType
import com.owenlejeune.tvtime.ui.screens.main.*

object NavConstants {
    const val ID_KEY = "id_key"
    const val TYPE_KEY = "type_key"
}

@Composable
fun MainNavigationRoutes(
    navController: NavHostController,
    startDestination: String = MainNavItem.MainView.route
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(MainNavItem.MainView.route) {
            MainAppView(appNavController = navController)
        }
        composable(
            MainNavItem.DetailView.route.plus("/{${NavConstants.TYPE_KEY}}/{${NavConstants.ID_KEY}}"),
            arguments = listOf(
                navArgument(NavConstants.ID_KEY) { type = NavType.IntType },
                navArgument(NavConstants.TYPE_KEY) { type = NavType.EnumType(MediaViewType::class.java) }
            )
        ) { navBackStackEntry ->
            val args = navBackStackEntry.arguments
            val mediaType = args?.getSerializable(NavConstants.TYPE_KEY) as MediaViewType
            if (mediaType != MediaViewType.PERSON) {
                MediaDetailView(
                    appNavController = navController,
                    itemId = args.getInt(NavConstants.ID_KEY),
                    type = mediaType
                )
            } else {
                PersonDetailView(
                    appNavController = navController,
                    personId = args.getInt(NavConstants.ID_KEY)
                )
            }
        }
    }
}

@Composable
fun MainNavGraph(
    appNavController: NavHostController,
    navController: NavHostController,
    appBarTitle: MutableState<String>,
    appBarActions: MutableState<@Composable (RowScope.() -> Unit)> = mutableStateOf({}),
    startDestination: String = BottomNavItem.Items[0].route
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(BottomNavItem.Movies.route) {
            appBarActions.value = {}
            MediaTab(appBarTitle = appBarTitle, appNavController = appNavController, mediaType = MediaViewType.MOVIE)
        }
        composable(BottomNavItem.TV.route) {
            appBarActions.value = {}
            MediaTab(appBarTitle = appBarTitle, appNavController = appNavController, mediaType = MediaViewType.TV)
        }
        composable(BottomNavItem.Account.route) {
            AccountTab(appBarTitle = appBarTitle, appNavController = appNavController, appBarActions = appBarActions)
        }
        composable(BottomNavItem.People.route) {
            appBarActions.value = {}
            PeopleTab(appBarTitle = appBarTitle, appNavController = appNavController)
        }
//        composable(BottomNavItem.Favourites.route) {
//            appBarActions.value = {}
//            FavouritesTab()
//        }
        composable(BottomNavItem.Settings.route) {
            appBarActions.value = {}
            SettingsTab(appBarTitle = appBarTitle)
        }
    }
}