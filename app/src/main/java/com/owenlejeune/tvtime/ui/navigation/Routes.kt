package com.owenlejeune.tvtime.ui.navigation

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.owenlejeune.tvtime.ui.screens.main.MediaViewType
import com.owenlejeune.tvtime.ui.screens.main.*
import com.owenlejeune.tvtime.utils.NavConstants

@Composable
fun MainNavGraph(
    activity: AppCompatActivity,
    appNavController: NavHostController,
    navController: NavHostController,
    fab: MutableState<@Composable () -> Unit>,
    appBarTitle: MutableState<@Composable () -> Unit>,
    appBarActions: MutableState<@Composable (RowScope.() -> Unit)> = mutableStateOf({}),
    startDestination: String = BottomNavItem.SortedItems[0].route
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(BottomNavItem.Movies.route) {
            appBarActions.value = {}
            MediaTab(
                appBarTitle = appBarTitle,
                appNavController = appNavController,
                mediaType = MediaViewType.MOVIE,
                fab = fab
            )
        }
        composable(BottomNavItem.TV.route) {
            appBarActions.value = {}
            MediaTab(
                appBarTitle = appBarTitle,
                appNavController = appNavController,
                mediaType = MediaViewType.TV,
                fab = fab
            )
        }
        composable(route = BottomNavItem.Account.route) {
            AccountViewContent(appNavController = appNavController)
            fab.value = {}
        }
        composable(BottomNavItem.People.route) {
            appBarActions.value = {}
            PeopleTab(
                appBarTitle = appBarTitle,
                appNavController = appNavController,
                fab = fab
            )
        }
    }
}