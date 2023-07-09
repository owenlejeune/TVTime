package com.owenlejeune.tvtime.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.ui.screens.DarkModePreferences
import com.owenlejeune.tvtime.ui.screens.DesignPreferences
import com.owenlejeune.tvtime.ui.screens.DevPreferences
import com.owenlejeune.tvtime.ui.screens.HomeScreenPreferences
import com.owenlejeune.tvtime.ui.screens.SearchPreferences
import com.owenlejeune.tvtime.ui.screens.SettingsHome
import com.owenlejeune.tvtime.ui.screens.SpecialFeaturePreferences

@Composable
fun SettingsNavigationHost(
    appNavController: NavController,
    appBarTitle: MutableState<String>,
    popBackAction: MutableState<() -> Unit>
) {
    val settingsNavController = rememberNavController()
    NavHost(
        navController = settingsNavController,
        startDestination = SettingsNavItem.Home.route,
        enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(500)) },
        popEnterTransition = { fadeIn(tween(500)) },
        exitTransition = { fadeOut(tween(500)) },
        popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(500)) }
    ) {
        composable(SettingsNavItem.Home.route) {
            appBarTitle.value = stringResource(id = R.string.nav_settings_title)
            popBackAction.value = { appNavController.popBackStack() }
            SettingsHome(settingsNavController = settingsNavController)
        }
        composable(SettingsNavItem.Search.route) {
            appBarTitle.value = stringResource(id = R.string.preference_heading_search)
            popBackAction.value = { settingsNavController.popBackStack() }
            SearchPreferences()
        }
        composable(SettingsNavItem.Design.route) {
            appBarTitle.value = stringResource(id = R.string.preference_heading_design)
            popBackAction.value = { settingsNavController.popBackStack() }
            DesignPreferences(settingsNavController = settingsNavController)
        }
        composable(SettingsNavItem.DarkMode.route) {
            appBarTitle.value = stringResource(id = R.string.preference_heading_dark_mode)
            popBackAction.value = { settingsNavController.popBackStack() }
            DarkModePreferences()
        }
        composable(SettingsNavItem.HomeScreen.route) {
            appBarTitle.value = stringResource(id = R.string.preference_heading_home_screen)
            popBackAction.value = { settingsNavController.popBackStack() }
            HomeScreenPreferences()
        }
        composable(SettingsNavItem.SpecialFeatures.route) {
            appBarTitle.value = stringResource(id = R.string.preference_heading_special_features)
            popBackAction.value = { settingsNavController.popBackStack() }
            SpecialFeaturePreferences()
        }
        composable(SettingsNavItem.Developer.route) {
            appBarTitle.value = stringResource(id = R.string.preferences_debug_title)
            popBackAction.value = { settingsNavController.popBackStack() }
            DevPreferences()
        }
    }
}

sealed class SettingsNavItem(val route: String) {
    object Home: SettingsNavItem("settings_home")
    object Search: SettingsNavItem("settings_search")
    object Design: SettingsNavItem("settings_design")
    object DarkMode: SettingsNavItem("settings_dark_mode")
    object HomeScreen: SettingsNavItem("settings_home_screen")
    object SpecialFeatures: SettingsNavItem("settings_special_feature")
    object Developer: SettingsNavItem("settings_developer")
}