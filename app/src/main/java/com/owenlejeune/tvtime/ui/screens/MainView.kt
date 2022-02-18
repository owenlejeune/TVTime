package com.owenlejeune.tvtime.ui.screens

import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.owenlejeune.tvtime.preferences.AppPreferences
import com.owenlejeune.tvtime.ui.components.RoundedTextField
import com.owenlejeune.tvtime.ui.components.SearchFab
import com.owenlejeune.tvtime.ui.navigation.BottomNavItem
import com.owenlejeune.tvtime.ui.navigation.BottomNavigationRoutes
import org.koin.java.KoinJavaComponent.get

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun MainAppView(appNavController: NavHostController, preferences: AppPreferences = get(AppPreferences::class.java)) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val appBarTitle = remember { mutableStateOf(BottomNavItem.Items[0].name) }
    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val scrollBehavior = remember(decayAnimationSpec) {
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(decayAnimationSpec)
    }

    val shouldShowSearch = remember { mutableStateOf(false) }
    val searchableScreens = listOf(BottomNavItem.Movies.route, BottomNavItem.TV.route)

    // todo - scroll state not remember when returing from detail screen

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
            if (currentRoute in searchableScreens) {
                SearchTopBar(
                    title = appBarTitle,
                    scrollBehavior = scrollBehavior,
                    shouldShowSearch = shouldShowSearch
                )
            } else {
                TopBar(
                    title = appBarTitle,
                    scrollBehavior = scrollBehavior
                )
            }
        },
        floatingActionButton = {
            if (currentRoute in searchableScreens && !preferences.persistentSearch) {
                SearchFab()
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            BottomNavigationRoutes(appNavController = appNavController, navController = navController)
        }
    }
}

@Composable
private fun TopBar(
    title: MutableState<String>,
    scrollBehavior: TopAppBarScrollBehavior
) {
    LargeTopAppBar(
        title = { Text(text = title.value) },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults
            .largeTopAppBarColors(
                scrolledContainerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.primary
            )
    )
}

@Composable
private fun SearchTopBar(
    title: MutableState<String>,
    scrollBehavior: TopAppBarScrollBehavior,
    hasSearchFocus: MutableState<Boolean> = remember { mutableStateOf(false) },
    shouldShowSearch: MutableState<Boolean> = remember { mutableStateOf(false) },
    preferences: AppPreferences = get(AppPreferences::class.java)
) {
    SmallTopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!hasSearchFocus.value && !(preferences.persistentSearch && preferences.hideTitle)) {
                    Text(text = title.value)
                }
                if (shouldShowSearch.value || preferences.persistentSearch) {
                    var textState by remember { mutableStateOf("") }
                    val basePadding = 8.dp
                    RoundedTextField(
                        modifier = Modifier
                            .padding(
                                end = if (hasSearchFocus.value || preferences.hideTitle) (basePadding * 3) else basePadding,
                                start = if (hasSearchFocus.value || preferences.hideTitle) 0.dp else basePadding
                            )

                            .height(35.dp)
                            .onFocusChanged { focusState ->
                                hasSearchFocus.value = focusState.isFocused
                            },
                        value = textState,
                        onValueChange = { textState = it },
                        placeHolder = "Search ${title.value}"
                    )
                }
            }
        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults
            .largeTopAppBarColors(
                scrolledContainerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.primary
            )
    )
}

@Composable
private fun BottomNavBar(navController: NavController, appBarTitle: MutableState<String>) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        BottomNavItem.Items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(painter = painterResource(id = item.icon), contentDescription = null) },
                label = { Text(item.name) },
                selected = currentRoute == item.route,
                onClick = {
                    onBottomAppBarItemClicked(
                        navController = navController,
                        appBarTitle = appBarTitle,
                        item = item
                    )
                },
                colors = NavigationBarItemDefaults
                    .colors(
                        selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                        indicatorColor = MaterialTheme.colorScheme.primary
                    )
            )
        }
    }

    appBarTitle.value = BottomNavItem.getByRoute(currentRoute)?.name ?: ""
}

private fun onBottomAppBarItemClicked(
    navController: NavController,
    appBarTitle: MutableState<String>,
    item: BottomNavItem
) {
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