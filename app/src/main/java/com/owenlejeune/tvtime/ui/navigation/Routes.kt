package com.owenlejeune.tvtime.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.owenlejeune.tvtime.ui.screens.DetailView
import com.owenlejeune.tvtime.ui.screens.MainAppView
import com.owenlejeune.tvtime.ui.screens.MediaViewType
import com.owenlejeune.tvtime.ui.screens.PersonDetailView
import com.owenlejeune.tvtime.ui.screens.tabs.bottom.AccountTab
import com.owenlejeune.tvtime.ui.screens.tabs.bottom.FavouritesTab
import com.owenlejeune.tvtime.ui.screens.tabs.bottom.MediaTab
import com.owenlejeune.tvtime.ui.screens.tabs.bottom.SettingsTab

object NavConstants {
    const val ID_KEY = "id_key"
    const val TYPE_KEY = "type_key"
}

@Composable
fun MainNavigationRoutes(navController: NavHostController, displayUnderStatusBar: MutableState<Boolean> = mutableStateOf(false)) {
    NavHost(navController = navController, startDestination = MainNavItem.MainView.route) {
        composable(MainNavItem.MainView.route) {
            displayUnderStatusBar.value = false
            MainAppView(appNavController = navController)
        }
        composable(
            MainNavItem.DetailView.route.plus("/{${NavConstants.TYPE_KEY}}/{${NavConstants.ID_KEY}}"),
            arguments = listOf(
                navArgument(NavConstants.ID_KEY) { type = NavType.IntType },
                navArgument(NavConstants.TYPE_KEY) { type = NavType.EnumType(MediaViewType::class.java) }
            )
        ) { navBackStackEntry ->
            displayUnderStatusBar.value = true
            val args = navBackStackEntry.arguments
            val mediaType = args?.getSerializable(NavConstants.TYPE_KEY) as MediaViewType
            if (mediaType != MediaViewType.PERSON) {
                DetailView(
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
fun BottomNavigationRoutes(
    appNavController: NavHostController,
    navController: NavHostController
) {
    NavHost(navController = navController, startDestination = BottomNavItem.Movies.route) {
        composable(BottomNavItem.Movies.route) {
            MediaTab(appNavController = appNavController, mediaType = MediaViewType.MOVIE)
        }
        composable(BottomNavItem.TV.route) {
            MediaTab(appNavController = appNavController, mediaType = MediaViewType.TV)
        }
        composable(BottomNavItem.Account.route) {
            AccountTab()
        }
        composable(BottomNavItem.Favourites.route) {
            FavouritesTab()
        }
        composable(BottomNavItem.Settings.route) {
            SettingsTab()
        }
    }
}