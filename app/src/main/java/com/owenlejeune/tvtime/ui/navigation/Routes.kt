package com.owenlejeune.tvtime.ui.navigation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.owenlejeune.tvtime.ui.screens.DetailView
import com.owenlejeune.tvtime.ui.screens.DetailViewType
import com.owenlejeune.tvtime.ui.screens.MainAppView
import com.owenlejeune.tvtime.ui.screens.tabs.FavouritesTab
import com.owenlejeune.tvtime.ui.screens.tabs.MoviesTab
import com.owenlejeune.tvtime.ui.screens.tabs.SettingsTab
import com.owenlejeune.tvtime.ui.screens.tabs.TvTab

@Composable
fun MainNavigationRoutes(navController: NavHostController, displayUnderStatusBar: MutableState<Boolean> = mutableStateOf(false)) {
    NavHost(navController = navController, startDestination = MainNavItem.MainView.route) {
        composable(MainNavItem.MainView.route) {
            displayUnderStatusBar.value = false
            MainAppView(appNavController = navController)
        }
        composable(
            MainNavItem.DetailView.route.plus("/{TYPE_KEY}/{ID_KEY}"),
            arguments = listOf(
                navArgument("ID_KEY") { type = NavType.IntType },
                navArgument("TYPE_KEY") { type = NavType.EnumType(DetailViewType::class.java) }
            )
        ) { navBackStackEntry ->
            displayUnderStatusBar.value = true
            val args = navBackStackEntry.arguments
            DetailView(
                appNavController = navController,
                itemId = args?.getInt("ID_KEY"),
                type = args?.getSerializable("TYPE_KEY") as DetailViewType
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