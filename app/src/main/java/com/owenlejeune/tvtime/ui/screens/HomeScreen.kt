package com.owenlejeune.tvtime.ui.screens

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.owenlejeune.tvtime.extensions.WindowSizeClass
import com.owenlejeune.tvtime.extensions.defaultNavBarColor
import com.owenlejeune.tvtime.extensions.navigateInBottomBar
import com.owenlejeune.tvtime.preferences.AppPreferences
import com.owenlejeune.tvtime.ui.components.AccountIcon
import com.owenlejeune.tvtime.ui.components.ProfileMenuContainer
import com.owenlejeune.tvtime.ui.components.ProfileMenuDefaults
import com.owenlejeune.tvtime.ui.components.TVTLargeTopAppBar
import com.owenlejeune.tvtime.ui.navigation.HomeScreenNavHost
import com.owenlejeune.tvtime.ui.navigation.HomeScreenNavItem
import com.owenlejeune.tvtime.ui.viewmodel.HomeScreenViewModel
import org.koin.java.KoinJavaComponent
import org.koin.java.KoinJavaComponent.get

object HomeScreen {
    val FLOATING_NAV_BAR_HEIGHT = 80.dp
    val FLOATING_NAV_BAR_OFFSET = (-24).dp
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    appNavController: NavHostController,
    mainNavStartRoute: String = HomeScreenNavItem.SortedItems[0].route,
    windowSize: WindowSizeClass,
    preferences: AppPreferences = get(AppPreferences::class.java)
) {
    val navController = rememberNavController()

    val topAppBarScrollState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarScrollState)

    val homeScreenViewModel = viewModel<HomeScreenViewModel>()

    val showProfileMenuOverlay = remember { mutableStateOf(false) }
    val navigationIcon = @Composable {
        AccountIcon(
            modifier = Modifier.padding(horizontal = 12.dp),
            size = 32.dp,
            onClick = { showProfileMenuOverlay.value = true }
        )
    }

    ProfileMenuContainer(
        appNavController = appNavController,
        visible = showProfileMenuOverlay.value,
        onDismissRequest = { showProfileMenuOverlay.value = false },
        colors = ProfileMenuDefaults.systemBarColors(navBarColor = MaterialTheme.colorScheme.defaultNavBarColor())
    ) {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                if (windowSize != WindowSizeClass.Expanded) {
                    TopBar(
                        scrollBehavior = scrollBehavior,
                        navigationIcon = navigationIcon,
                        appNavController = appNavController
                    )
                }
            },
            floatingActionButton = {
                val fab = remember { homeScreenViewModel.fab }
                fab.value()
            },
            bottomBar = {
                if (windowSize != WindowSizeClass.Expanded && !preferences.floatingBottomBar) {
                    BottomNavBar(navController = navController)
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                MainContent(
                    windowSize = windowSize,
                    appNavController = appNavController,
                    navController = navController,
                    topBarScrollBehaviour = scrollBehavior,
                    mainNavStartRoute = mainNavStartRoute,
                    navigationIcon = navigationIcon
                )

                if (windowSize != WindowSizeClass.Expanded && preferences.floatingBottomBar) {
                    FloatingBottomNavBar(navController = navController, modifier = Modifier.align(Alignment.BottomCenter))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    appNavController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    navigationIcon: @Composable () -> Unit = {}
) {
    val homeScreenViewModel = viewModel<HomeScreenViewModel>()
    val title = remember { homeScreenViewModel.appBarTitle }
    val actions = remember { homeScreenViewModel.appBarActions }

    TVTLargeTopAppBar(
        title = { Text(text = title.value) },
        scrollBehavior = scrollBehavior,
        appNavController = appNavController,
        actions = actions.value,
        navigationIcon = navigationIcon
    )
}

@Composable
private fun FloatingBottomNavBar(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    preferences: AppPreferences = get(AppPreferences::class.java)
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(
        modifier = modifier.then(
            Modifier
                .fillMaxWidth()
                .height(HomeScreen.FLOATING_NAV_BAR_HEIGHT)
                .padding(horizontal = 24.dp)
                .offset(y = HomeScreen.FLOATING_NAV_BAR_OFFSET)
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.defaultNavBarColor())
        )
    ) {
        Row(
            modifier = Modifier.align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HomeScreenNavItem.SortedItems.forEach { item ->
                val isSelected = currentRoute == item.route
                NavigationBarItem(
                    modifier = Modifier.clip(RoundedCornerShape(25.dp)),
                    icon = { Icon(imageVector = item.icon, contentDescription = null) },
                    label = {
                        if (preferences.showBottomTabLabels) {
                            Text(text = item.name)
                        }
                    },
                    selected = isSelected,
                    onClick = {
                        if (!isSelected) {
                            navController.navigateInBottomBar(item.route)
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = MaterialTheme.colorScheme.secondary,
                        selectedIconColor = MaterialTheme.colorScheme.onSecondary
                    )
                )
            }
        }
    }
}

@Composable
private fun BottomNavBar(
    navController: NavController,
    preferences: AppPreferences = get(AppPreferences::class.java)
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        HomeScreenNavItem.SortedItems.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                modifier = Modifier
                    .padding(4.dp)
                    .clip(RoundedCornerShape(24.dp)),
                icon = { Icon(imageVector = item.icon, contentDescription = null) },
                label = {
                    val name = if (preferences.showBottomTabLabels) item.name else " "
                    Text(text = name)
                },
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        navController.navigateInBottomBar(item.route)
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.secondary,
                    selectedIconColor = MaterialTheme.colorScheme.onSecondary
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainContent(
    windowSize: WindowSizeClass,
    appNavController: NavHostController,
    navController: NavHostController,
    topBarScrollBehaviour: TopAppBarScrollBehavior,
    navigationIcon: @Composable () -> Unit = {},
    mainNavStartRoute: String = HomeScreenNavItem.SortedItems[0].route
) {
    if (windowSize == WindowSizeClass.Expanded) {
        DualColumnMainContent(
            appNavController = appNavController,
            navController = navController,
            topBarScrollBehaviour = topBarScrollBehaviour,
            mainNavStartRoute = mainNavStartRoute,
            navigationIcon = navigationIcon
        )
    } else {
        SingleColumnMainContent(
            appNavController = appNavController,
            navController = navController,
            mainNavStartRoute = mainNavStartRoute
        )
    }
}

@Composable
private fun SingleColumnMainContent(
    appNavController: NavHostController,
    navController: NavHostController,
    mainNavStartRoute: String = HomeScreenNavItem.SortedItems[0].route
) {
    MainMediaView(
        appNavController = appNavController,
        navController = navController,
        mainNavStartRoute = mainNavStartRoute
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DualColumnMainContent(
    appNavController: NavHostController,
    navController: NavHostController,
    topBarScrollBehaviour: TopAppBarScrollBehavior,
    navigationIcon: @Composable () -> Unit = {},
    mainNavStartRoute: String = HomeScreenNavItem.SortedItems[0].route,
    preferences: AppPreferences = get(AppPreferences::class.java)
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Row(modifier = Modifier.fillMaxSize()) {
        NavigationRail {
            Spacer(modifier = Modifier.weight(1f))
            HomeScreenNavItem.SortedItems.forEachIndexed { index, item ->
                val isSelected = currentRoute == item.route
                NavigationRailItem(
                    icon = { Icon(imageVector = item.icon, contentDescription = null) },
                    label = { if (preferences.showBottomTabLabels) Text(item.name) },
                    selected = isSelected,
                    onClick = {
                        if (!isSelected) {
                            navController.navigateInBottomBar(item.route)
                        }
                    },
                    colors = NavigationRailItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.secondary)
                )
                if (index < HomeScreenNavItem.SortedItems.size - 1) {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
            Spacer(modifier = Modifier.weight(1f))
        }
        Column {
            TopBar(
                scrollBehavior = topBarScrollBehaviour,
                navigationIcon = navigationIcon,
                appNavController = appNavController
            )
            MainMediaView(
                appNavController = appNavController,
                navController = navController,
                mainNavStartRoute = mainNavStartRoute
            )
        }
    }
}

@Composable
private fun MainMediaView(
    appNavController: NavHostController,
    navController: NavHostController,
    mainNavStartRoute: String = HomeScreenNavItem.SortedItems[0].route
) {
    val activity = LocalContext.current as Activity
    Column {
        BackHandler(enabled = true) {
            activity.finish()
        }

        HomeScreenNavHost(
            appNavController = appNavController,
            navController = navController,
            startDestination = mainNavStartRoute
        )
    }
}