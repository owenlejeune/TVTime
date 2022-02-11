package com.owenlejeune.tvtime.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.owenlejeune.tvtime.ui.screens.*
import com.owenlejeune.tvtime.ui.screens.tabs.FavouritesTab
import com.owenlejeune.tvtime.ui.screens.tabs.MoviesTab
import com.owenlejeune.tvtime.ui.screens.tabs.SettingsTab
import com.owenlejeune.tvtime.ui.screens.tabs.TvTab

@Composable
fun MainNavigationRoutes(navController: NavHostController) {
    NavHost(navController = navController, startDestination = MainNavItem.MainView.route) {
        composable(MainNavItem.MainView.route) {
            MainAppView(appNavController = navController)
        }
        composable(
            MainNavItem.DetailView.route.plus("/{ID_KEY}"),
            arguments = listOf(navArgument("ID_KEY") { type = NavType.IntType })
        ) { navBackStackEntry ->
            val args = navBackStackEntry.arguments
            DetailView(
                appNavController = navController,
                itemId = navBackStackEntry.arguments?.getInt("ID_KEY")
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