package com.owenlejeune.tvtime

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.owenlejeune.tvtime.ui.components.NavItems
import com.owenlejeune.tvtime.ui.screens.FavouritesTab
import com.owenlejeune.tvtime.ui.screens.MoviesTab
import com.owenlejeune.tvtime.ui.screens.SettingsTab
import com.owenlejeune.tvtime.ui.screens.TvTab
import com.owenlejeune.tvtime.ui.theme.TVTimeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp() {
    TVTimeTheme {
        val navController = rememberNavController()
        val appBarTitle = remember { mutableStateOf(NavItems.Items[0].name) }
        val decayAnimationSpec = rememberSplineBasedDecay<Float>()
        val scrollBehavior = remember(decayAnimationSpec) {
            TopAppBarDefaults.exitUntilCollapsedScrollBehavior(decayAnimationSpec)
        }

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            backgroundColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                BottomNavBar(
                    navController = navController,
                    appBarTitle = appBarTitle
                )
            },
            topBar = {
                TopBar(
                    title = appBarTitle,
                    scrollBehavior = scrollBehavior
                )
            },
            floatingActionButton = {
                SearchFab()
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                NavigationRoutes(navController = navController)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(title: MutableState<String>, scrollBehavior: TopAppBarScrollBehavior) {
    LargeTopAppBar(
        title = { Text(text = title.value) },
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun BottomNavBar(navController: NavController, appBarTitle: MutableState<String>) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        NavItems.Items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(painter = painterResource(id = item.icon), contentDescription = null) },
                label = { Text(item.name) },
                selected = currentRoute == item.route,
                onClick = {
                    appBarTitle.value = item.name
                    navController.navigate(item.route) {
                        navController.graph.startDestinationRoute?.let { screenRoute ->
                            popUpTo(screenRoute) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
private fun NavigationRoutes(navController: NavHostController) {
    NavHost(navController = navController, startDestination = NavItems.Movies.route) {
        composable(NavItems.Movies.route) {
            MoviesTab()
        }
        composable(NavItems.TV.route) {
            TvTab()
        }
        composable(NavItems.Favourites.route) {
            FavouritesTab()
        }
        composable(NavItems.Settings.route) {
            SettingsTab()
        }
    }
}

@Composable
private fun SearchFab() {
    val context = LocalContext.current
    FloatingActionButton(onClick = {
        Toast.makeText(context, "Search Clicked!", Toast.LENGTH_SHORT).show()
    }) {
        Icon(Icons.Filled.Search, "")
    }
}

@Preview(showBackground = true)
@Composable
fun MyAppPreview() {
    MyApp()
}