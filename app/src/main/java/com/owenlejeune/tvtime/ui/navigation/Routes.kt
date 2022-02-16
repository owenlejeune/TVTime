package com.owenlejeune.tvtime.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.owenlejeune.tvtime.ui.screens.DetailView
import com.owenlejeune.tvtime.ui.screens.DetailViewType
import com.owenlejeune.tvtime.ui.screens.MainAppView
import com.owenlejeune.tvtime.ui.screens.tabs.FavouritesTab
import com.owenlejeune.tvtime.ui.screens.tabs.MoviesTab
import com.owenlejeune.tvtime.ui.screens.tabs.SettingsTab
import com.owenlejeune.tvtime.ui.screens.tabs.TvTab

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
                navArgument(NavConstants.TYPE_KEY) { type = NavType.EnumType(DetailViewType::class.java) }
            )
        ) { navBackStackEntry ->
            displayUnderStatusBar.value = true
            val args = navBackStackEntry.arguments
            DetailView(
                appNavController = navController,
                itemId = args?.getInt(NavConstants.ID_KEY),
                type = args?.getSerializable(NavConstants.TYPE_KEY) as DetailViewType
            )
        }
    }
}

@Composable
fun BottomNavigationRoutes(
    appNavController: NavController,
    navController: NavHostController
) {
    NavHost(navController = navController, startDestination = BottomNavItem.Movies.route) {
        composable(BottomNavItem.Movies.route) {
            MoviesTab(appNavController = appNavController)
        }
        composable(BottomNavItem.TV.route) {
            TvTab(appNavController = appNavController)
        }
        composable(BottomNavItem.Favourites.route) {
            FavouritesTab()
        }
        composable(BottomNavItem.Settings.route) {
            SettingsTab()
        }
    }
}