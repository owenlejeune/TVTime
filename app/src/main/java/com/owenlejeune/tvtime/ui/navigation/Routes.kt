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
            DetailView(
                appNavController = navController,
                itemId = args?.getInt(NavConstants.ID_KEY),
                type = args?.getSerializable(NavConstants.TYPE_KEY) as MediaViewType
            )
        }
    }
}

@Composable
fun BottomNavigationRoutes(
    appNavController: NavHostController,
    navController: NavHostController,
    shouldShowSearch: MutableState<Boolean>
) {
    NavHost(navController = navController, startDestination = BottomNavItem.Movies.route) {
        composable(BottomNavItem.Movies.route) {
            shouldShowSearch.value = true
            MediaTab(appNavController = appNavController, mediaType = MediaViewType.MOVIE)
        }
        composable(BottomNavItem.TV.route) {
            shouldShowSearch.value = true
            MediaTab(appNavController = appNavController, mediaType = MediaViewType.TV)
        }
        composable(BottomNavItem.Favourites.route) {
            shouldShowSearch.value = false
            FavouritesTab()
        }
        composable(BottomNavItem.Settings.route) {
            shouldShowSearch.value = false
            SettingsTab()
        }
    }
}