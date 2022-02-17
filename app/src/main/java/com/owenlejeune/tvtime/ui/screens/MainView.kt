package com.owenlejeune.tvtime.ui.screens

import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.layout.*
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
import com.owenlejeune.tvtime.ui.components.RoundedTextField
import com.owenlejeune.tvtime.ui.components.SearchFab
import com.owenlejeune.tvtime.ui.navigation.BottomNavItem
import com.owenlejeune.tvtime.ui.navigation.BottomNavigationRoutes

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun MainAppView(appNavController: NavHostController) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val appBarTitle = remember { mutableStateOf(BottomNavItem.Items[0].name) }
    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val scrollBehavior = remember(decayAnimationSpec) {
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(decayAnimationSpec)
    }

    val shouldShowSearch = remember { mutableStateOf(true) }

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
            TopBar(
                title = appBarTitle,
                scrollBehavior = scrollBehavior,
                shouldShowSearch = shouldShowSearch
            )
        },
        floatingActionButton = {
            if (currentRoute in listOf(BottomNavItem.Movies.route, BottomNavItem.TV.route)) {
                SearchFab()
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            BottomNavigationRoutes(appNavController = appNavController, navController = navController, shouldShowSearch = shouldShowSearch)
        }
    }
}

@Composable
private fun TopBar(
    title: MutableState<String>,
    scrollBehavior: TopAppBarScrollBehavior,
    hasSearchFocus: MutableState<Boolean> = remember { mutableStateOf(false) },
    shouldShowSearch: MutableState<Boolean> = remember { mutableStateOf(true) }
) {
    SmallTopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!hasSearchFocus.value) {
                    Text(text = title.value)
                }
                if (shouldShowSearch.value) {
                    var textState by remember { mutableStateOf("") }
                    val basePadding = 8.dp
                    RoundedTextField(
                        modifier = Modifier
                            .padding(
                                end = if (hasSearchFocus.value) (basePadding * 2) else basePadding,
                                start = if (hasSearchFocus.value) 0.dp else basePadding
                            )

                            .height(35.dp)
                            .onFocusChanged { focusState ->
                                hasSearchFocus.value = focusState.isFocused
                            },
                        value = textState,
                        onValueChange = { textState = it },
                        placeHolder = "Search ${title.value.lowercase()}"
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