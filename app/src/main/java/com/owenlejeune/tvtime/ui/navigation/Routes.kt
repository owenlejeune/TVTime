package com.owenlejeune.tvtime.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.owenlejeune.tvtime.MainAppView
import com.owenlejeune.tvtime.ui.screens.FavouritesTab
import com.owenlejeune.tvtime.ui.screens.MoviesTab
import com.owenlejeune.tvtime.ui.screens.SettingsTab
import com.owenlejeune.tvtime.ui.screens.TvTab

@Composable
fun MainNavigationRoutes(navController: NavHostController) {
    NavHost(navController = navController, startDestination = MainNavItem.MainView.route) {
        composable(MainNavItem.MainView.route) {
            MainAppView(appNavController = navController)
        }
        composable(MainNavItem.DetailView.route) {

        }
    }
}

@Composable
fun BottomNavigationRoutes(navController: NavHostController) {
    NavHost(navController = navController, startDestination = BottomNavItem.Movies.route) {
        composable(BottomNavItem.Movies.route) {
            MoviesTab()
        }
        composable(BottomNavItem.TV.route) {
            TvTab()
        }
        composable(BottomNavItem.Favourites.route) {
            FavouritesTab()
        }
        composable(BottomNavItem.Settings.route) {
            SettingsTab()
        }
    }
}