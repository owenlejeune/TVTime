package com.owenlejeune.tvtime.ui.screens

import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.preferences.AppPreferences
import com.owenlejeune.tvtime.ui.components.RoundedTextField
import com.owenlejeune.tvtime.ui.components.SearchFab
import com.owenlejeune.tvtime.ui.navigation.BottomNavItem
import com.owenlejeune.tvtime.ui.navigation.MainNavGraph
import com.owenlejeune.tvtime.utils.KeyboardManager
import org.koin.java.KoinJavaComponent.get

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun MainAppView(appNavController: NavHostController, preferences: AppPreferences = get(AppPreferences::class.java)) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val appBarTitle = rememberSaveable { mutableStateOf(BottomNavItem.getByRoute(currentRoute)?.name ?: BottomNavItem.Items[0].name) }
    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val topAppBarScrollState = rememberTopAppBarScrollState()
    val scrollBehavior = remember(decayAnimationSpec) {
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(decayAnimationSpec, topAppBarScrollState)
    }

    val focusRequester = remember { FocusRequester() }
    val focusSearchBar = rememberSaveable { mutableStateOf(false) }
    val searchableScreens = listOf(BottomNavItem.Movies.route, BottomNavItem.TV.route, BottomNavItem.People.route)

    val appBarActions = remember { mutableStateOf<@Composable RowScope.() -> Unit>( {} ) }

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
                    requestSearchFocus = focusSearchBar,
                    focusRequester = focusRequester
                )
            } else {
                TopBar(
                    title = appBarTitle,
                    scrollBehavior = scrollBehavior,
                    appBarActions = appBarActions
                )
            }
        },
        floatingActionButton = {
            if (currentRoute in searchableScreens && !preferences.persistentSearch && !focusSearchBar.value) {
                SearchFab(
                    focusSearchBar = focusSearchBar,
                    focusRequester = focusRequester
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            MainNavGraph(appNavController = appNavController, navController = navController, appBarTitle = appBarTitle, appBarActions = appBarActions)
        }
    }
}

@Composable
private fun TopBar(
    title: MutableState<String>,
    scrollBehavior: TopAppBarScrollBehavior,
    appBarActions: MutableState<@Composable (RowScope.() -> Unit)> = mutableStateOf({})
) {
    LargeTopAppBar(
        title = { Text(text = title.value) },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults
            .largeTopAppBarColors(
                scrolledContainerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.primary
            ),
        actions = appBarActions.value
    )
}

@Composable
private fun SearchTopBar(
    title: MutableState<String>,
    scrollBehavior: TopAppBarScrollBehavior,
    requestSearchFocus: MutableState<Boolean> = remember { mutableStateOf(false) },
    focusRequester: FocusRequester = remember { FocusRequester() },
    preferences: AppPreferences = get(AppPreferences::class.java)
) {
    SmallTopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                val hasSearchFocus = rememberSaveable { mutableStateOf(requestSearchFocus.value) }
                if (!requestSearchFocus.value && !hasSearchFocus.value && !(preferences.persistentSearch && preferences.hideTitle)) {
                    Text(text = title.value)
                }
                if (requestSearchFocus.value || preferences.persistentSearch) {
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
                        requestFocus = requestSearchFocus.value,
                        focusRequester = focusRequester,
                        value = textState,
                        onValueChange = { textState = it },
                        placeHolder = stringResource(id = R.string.search_placeholder, title.value),
                        trailingIcon = {
                            Image(
                                painter = painterResource(id = R.drawable.ic_search),
                                contentDescription = stringResource(R.string.search_icon_content_descriptor),
                                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary)
                            )
                        }
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

    val context = LocalContext.current
    val keyboardManager = KeyboardManager.getInstance(context)
    keyboardManager.attachKeyboardDismissListener {
        requestSearchFocus.value = false
    }
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
                }
            )
        }
    }
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