package com.owenlejeune.tvtime.ui.screens

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.owenlejeune.tvtime.extensions.WindowSizeClass
import com.owenlejeune.tvtime.extensions.navigateInBottomBar
import com.owenlejeune.tvtime.preferences.AppPreferences
import com.owenlejeune.tvtime.ui.components.AccountIcon
import com.owenlejeune.tvtime.ui.components.ProfileMenuContainer
import com.owenlejeune.tvtime.ui.components.ProfileMenuDefaults
import com.owenlejeune.tvtime.ui.navigation.HomeScreenNavItem
import com.owenlejeune.tvtime.ui.navigation.HomeScreenNavHost
import org.koin.java.KoinJavaComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    appNavController: NavHostController,
    mainNavStartRoute: String = HomeScreenNavItem.SortedItems[0].route,
    windowSize: WindowSizeClass
) {
    val navController = rememberNavController()

    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val topAppBarScrollState = rememberTopAppBarScrollState()
    val scrollBehavior = remember(decayAnimationSpec) {
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(decayAnimationSpec, topAppBarScrollState)
    }

    val appBarTitle = remember { mutableStateOf<@Composable () -> Unit>({}) }
    val appBarActions = remember { mutableStateOf<@Composable RowScope.() -> Unit>({}) }
    val fab = remember { mutableStateOf<@Composable () -> Unit>({}) }

    val showProfileMenuOverlay = remember { mutableStateOf(false) }
    val navigationIcon = @Composable {
        AccountIcon(
            modifier = Modifier.padding(horizontal = 12.dp),
            size = 32.dp,
            onClick = { showProfileMenuOverlay.value = true }
        )
    }

    val defaultNavBarColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f).compositeOver(background = MaterialTheme.colorScheme.surface)

    ProfileMenuContainer(
        appNavController = appNavController,
        visible = showProfileMenuOverlay.value,
        onDismissRequest = { showProfileMenuOverlay.value = false },
        colors = ProfileMenuDefaults.systemBarColors(navBarColor = defaultNavBarColor)
    ) {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                if (windowSize != WindowSizeClass.Expanded) {
                    TopBar(
                        title = appBarTitle.value,
                        scrollBehavior = scrollBehavior,
                        appBarActions = appBarActions,
                        navigationIcon = navigationIcon
                    )
                }
            },
            floatingActionButton = {
                fab.value()
            },
            bottomBar = {
                if (windowSize != WindowSizeClass.Expanded) {
                    BottomNavBar(navController = navController)
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                MainContent(
                    windowSize = windowSize,
                    appNavController = appNavController,
                    navController = navController,
                    fab = fab,
                    appBarTitle = appBarTitle,
                    appBarActions = appBarActions,
                    topBarScrollBehaviour = scrollBehavior,
                    mainNavStartRoute = mainNavStartRoute,
                    navigationIcon = navigationIcon
                )
            }
        }
    }
}

@Composable
private fun TopBar(
    title: @Composable () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    appBarActions: MutableState<@Composable (RowScope.() -> Unit)> = mutableStateOf({}),
    navigationIcon: @Composable () -> Unit = {}
) {
    LargeTopAppBar(
        title = title,
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults
            .largeTopAppBarColors(
                scrolledContainerColor = MaterialTheme.colorScheme.background
            ),
        actions = {
            appBarActions.value(this)
        },
        navigationIcon = navigationIcon
    )
}

@Composable
private fun BottomNavBar(
    navController: NavController,
    preferences: AppPreferences = KoinJavaComponent.get(AppPreferences::class.java)
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
                icon = { Icon(painter = painterResource(id = item.icon), contentDescription = null) },
                label = {
                    val name = if (preferences.showBottomTabLabels) item.name else " "
                    Text(text = name)
                },
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        navController.navigateInBottomBar(item.route)
                    }
                }
            )
        }
    }
}

@Composable
private fun MainContent(
    windowSize: WindowSizeClass,
    appNavController: NavHostController,
    navController: NavHostController,
    fab: MutableState<@Composable () -> Unit>,
    topBarScrollBehaviour: TopAppBarScrollBehavior,
    appBarTitle: MutableState<@Composable () -> Unit>,
    appBarActions: MutableState<@Composable (RowScope.() -> Unit)> = mutableStateOf({}),
    navigationIcon: @Composable () -> Unit = {},
    mainNavStartRoute: String = HomeScreenNavItem.SortedItems[0].route
) {
    if (windowSize == WindowSizeClass.Expanded) {
        DualColumnMainContent(
            appNavController = appNavController,
            navController = navController,
            fab = fab,
            appBarTitle = appBarTitle,
            appBarActions = appBarActions,
            topBarScrollBehaviour = topBarScrollBehaviour,
            mainNavStartRoute = mainNavStartRoute,
            navigationIcon = navigationIcon
        )
    } else {
        SingleColumnMainContent(
            appNavController = appNavController,
            navController = navController,
            fab = fab,
            appBarTitle = appBarTitle,
            appBarActions = appBarActions,
            mainNavStartRoute = mainNavStartRoute
        )
    }
}

@Composable
private fun SingleColumnMainContent(
    appNavController: NavHostController,
    navController: NavHostController,
    fab: MutableState<@Composable () -> Unit>,
    appBarTitle: MutableState<@Composable () -> Unit>,
    appBarActions: MutableState<@Composable (RowScope.() -> Unit)> = mutableStateOf({}),
    mainNavStartRoute: String = HomeScreenNavItem.SortedItems[0].route
) {
    MainMediaView(
        appNavController = appNavController,
        navController = navController,
        fab = fab,
        appBarTitle = appBarTitle,
        appBarActions = appBarActions,
        mainNavStartRoute = mainNavStartRoute
    )
}

@Composable
private fun DualColumnMainContent(
    appNavController: NavHostController,
    navController: NavHostController,
    fab: MutableState<@Composable () -> Unit>,
    topBarScrollBehaviour: TopAppBarScrollBehavior,
    appBarTitle: MutableState<@Composable () -> Unit>,
    appBarActions: MutableState<@Composable (RowScope.() -> Unit)> = mutableStateOf({}),
    navigationIcon: @Composable () -> Unit = {},
    mainNavStartRoute: String = HomeScreenNavItem.SortedItems[0].route,
    preferences: AppPreferences = KoinJavaComponent.get(AppPreferences::class.java)
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Row(modifier = Modifier.fillMaxSize()) {
        NavigationRail {
            Spacer(modifier = Modifier.weight(1f))
            HomeScreenNavItem.SortedItems.forEachIndexed { index, item ->
                val isSelected = currentRoute == item.route
                NavigationRailItem(
                    icon = { Icon(painter = painterResource(id = item.icon), contentDescription = null) },
                    label = { if (preferences.showBottomTabLabels) Text(item.name) },
                    selected = isSelected,
                    onClick = {
                        if (!isSelected) {
                            navController.navigateInBottomBar(item.route)
                        }
                    }
                )
                if (index < HomeScreenNavItem.SortedItems.size - 1) {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
            Spacer(modifier = Modifier.weight(1f))
        }
        Column {
            TopBar(
                title = appBarTitle.value,
                scrollBehavior = topBarScrollBehaviour,
                appBarActions = appBarActions,
                navigationIcon = navigationIcon
            )
            MainMediaView(
                appNavController = appNavController,
                navController = navController,
                fab = fab,
                appBarTitle = appBarTitle,
                appBarActions = appBarActions,
                mainNavStartRoute = mainNavStartRoute
            )
        }
    }
}

@Composable
private fun MainMediaView(
    appNavController: NavHostController,
    navController: NavHostController,
    fab: MutableState<@Composable () -> Unit>,
    appBarTitle: MutableState<@Composable () -> Unit>,
    appBarActions: MutableState<RowScope.() -> Unit> = mutableStateOf({}),
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
            fab = fab,
            appBarTitle = appBarTitle,
            appBarActions = appBarActions,
            startDestination = mainNavStartRoute
        )
    }
}