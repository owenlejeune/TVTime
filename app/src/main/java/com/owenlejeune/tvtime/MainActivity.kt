package com.owenlejeune.tvtime

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kieronquinn.monetcompat.app.MonetCompatActivity
import com.owenlejeune.tvtime.extensions.WindowSizeClass
import com.owenlejeune.tvtime.extensions.rememberWindowSizeClass
import com.owenlejeune.tvtime.preferences.AppPreferences
import com.owenlejeune.tvtime.ui.navigation.BottomNavItem
import com.owenlejeune.tvtime.ui.navigation.MainNavGraph
import com.owenlejeune.tvtime.ui.navigation.MainNavItem
import com.owenlejeune.tvtime.ui.screens.SearchScreen
import com.owenlejeune.tvtime.ui.screens.main.MediaDetailView
import com.owenlejeune.tvtime.ui.screens.main.MediaViewType
import com.owenlejeune.tvtime.ui.screens.main.PersonDetailView
import com.owenlejeune.tvtime.ui.screens.main.SettingsTab
import com.owenlejeune.tvtime.ui.theme.TVTimeTheme
import com.owenlejeune.tvtime.utils.KeyboardManager
import com.owenlejeune.tvtime.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.get

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : MonetCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CoroutineScope(Dispatchers.IO).launch {
            SessionManager.initialize()
        }

        var mainNavStartRoute = BottomNavItem.Items[0].route
        intent.data?.let {
            when (it.host) {
                getString(R.string.intent_route_auth_return) -> mainNavStartRoute = BottomNavItem.Account.route
            }
        }

        lifecycleScope.launchWhenCreated {
            monet.awaitMonetReady()
            setContent {
                AppKeyboardFocusManager()
                TVTimeTheme(monetCompat = monet) {
                    val appNavController = rememberNavController()
                    Box {
                       MainNavigationRoutes(appNavController = appNavController, mainNavStartRoute = mainNavStartRoute)
                    }
                }
            }
        }
    }

    @Composable
    private fun AppScaffold(
        appNavController: NavHostController,
        mainNavStartRoute: String = BottomNavItem.Items[0].route,
        preferences: AppPreferences = get(AppPreferences::class.java)
    ) {
        val windowSize = rememberWindowSizeClass()

        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        val appBarTitle = rememberSaveable { mutableStateOf(BottomNavItem.getByRoute(currentRoute)?.name ?: BottomNavItem.Items[0].name) }
        val decayAnimationSpec = rememberSplineBasedDecay<Float>()
        val topAppBarScrollState = rememberTopAppBarScrollState()
        val scrollBehavior = remember(decayAnimationSpec) {
            TopAppBarDefaults.exitUntilCollapsedScrollBehavior(decayAnimationSpec, topAppBarScrollState)
        }

        val appBarActions = remember { mutableStateOf<@Composable RowScope.() -> Unit>({}) }
        val fab = remember { mutableStateOf<@Composable () -> Unit>({}) }

        // todo - scroll state not remember when returing from detail screen

        Scaffold (
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                if (windowSize != WindowSizeClass.Expanded) {
                    TopBar(
                        appNavController = appNavController,
                        title = appBarTitle,
                        scrollBehavior = scrollBehavior,
                        appBarActions = appBarActions
                    )
                }
            },
            floatingActionButton = {
                fab.value()
            },
            bottomBar = {
                if (windowSize != WindowSizeClass.Expanded) {
                    BottomNavBar(navController = navController, appBarTitle = appBarTitle)
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
                    mainNavStartRoute = mainNavStartRoute
                )
            }
        }
    }

    @Composable
    private fun TopBar(
        appNavController: NavHostController,
        title: MutableState<String>,
        scrollBehavior: TopAppBarScrollBehavior,
        appBarActions: MutableState<@Composable (RowScope.() -> Unit)> = mutableStateOf({})
    ) {
        val defaultAppBarActions: @Composable RowScope.() -> Unit = {
            IconButton(
                onClick = {
                    appNavController.navigate(MainNavItem.SettingsView.route)
                }
            ) {
                Icon(imageVector = Icons.Filled.Settings, contentDescription = stringResource(id = R.string.nav_settings_title))
            }
        }
        LargeTopAppBar(
            title = { Text(text = title.value) },
            scrollBehavior = scrollBehavior,
            colors = TopAppBarDefaults
                .largeTopAppBarColors(
                    scrolledContainerColor = MaterialTheme.colorScheme.background
                ),
            actions = {
                defaultAppBarActions()
                appBarActions.value(this)
            }
        )
    }

    @Composable
    private fun BottomNavBar(navController: NavController, appBarTitle: MutableState<String>) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        NavigationBar {
            BottomNavItem.Items.forEach { item ->
                NavigationBarItem(
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(RoundedCornerShape(24.dp)),
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
        navigateToRoute(navController, item.route)
        appBarTitle.value = item.name
    }

    private fun navigateToRoute(navController: NavController, route: String) {
        navController.navigate(route) {
            navController.graph.startDestinationRoute?.let { screenRoute ->
                popUpTo(screenRoute) {
                    saveState = true
                }
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    @Composable
    private fun MainContent(
        windowSize: WindowSizeClass,
        appNavController: NavHostController,
        navController: NavHostController,
        fab: MutableState<@Composable () -> Unit>,
        topBarScrollBehaviour: TopAppBarScrollBehavior,
        appBarTitle: MutableState<String>,
        appBarActions: MutableState<@Composable (RowScope.() -> Unit)> = mutableStateOf({}),
        mainNavStartRoute: String = BottomNavItem.Items[0].route
    ) {
        if (windowSize == WindowSizeClass.Expanded) {
            DualColumnMainContent(
                appNavController = appNavController,
                navController = navController,
                fab = fab,
                appBarTitle = appBarTitle,
                appBarActions = appBarActions,
                topBarScrollBehaviour = topBarScrollBehaviour,
                mainNavStartRoute = mainNavStartRoute
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
        appBarTitle: MutableState<String>,
        appBarActions: MutableState<@Composable (RowScope.() -> Unit)> = mutableStateOf({}),
        mainNavStartRoute: String = BottomNavItem.Items[0].route
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
        appBarTitle: MutableState<String>,
        appBarActions: MutableState<@Composable (RowScope.() -> Unit)> = mutableStateOf({}),
        mainNavStartRoute: String = BottomNavItem.Items[0].route
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        Row(modifier = Modifier.fillMaxSize()) {
            NavigationRail {
                Spacer(modifier = Modifier.weight(1f))
                BottomNavItem.Items.forEach { item ->
                    NavigationRailItem(
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
                Spacer(modifier = Modifier.weight(1f))
            }
            Column {
                TopBar(
                    appNavController = appNavController,
                    title = appBarTitle,
                    scrollBehavior = topBarScrollBehaviour,
                    appBarActions = appBarActions
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
        appBarTitle: MutableState<String>,
        appBarActions: MutableState<RowScope.() -> Unit> = mutableStateOf({}),
        mainNavStartRoute: String = BottomNavItem.Items[0].route
    ) {
        Column {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

//            if (currentRoute in searchableScreens) {
//                SearchBar(appBarTitle.value)
//            }

            MainNavGraph(
                activity = this@MainActivity,
                appNavController = appNavController,
                navController = navController,
                fab = fab,
                appBarTitle = appBarTitle,
                appBarActions = appBarActions,
                startDestination = mainNavStartRoute
            )
        }
    }

    private object NavConstants {
        const val ID_KEY = "id_key"
        const val TYPE_KEY = "type_key"
        const val SETTINGS_KEY = "settings_key"
        const val SEARCH_ID_KEY = "search_key"
        const val SEARCH_TITLE_KEY = "search_title_key"
    }

    @Composable
    private fun MainNavigationRoutes(
        startDestination: String = MainNavItem.MainView.route,
        mainNavStartRoute: String = MainNavItem.Items[0].route,
        appNavController: NavHostController,
        preferences: AppPreferences = get(AppPreferences::class.java)
    ) {
        NavHost(navController = appNavController, startDestination = startDestination) {
            composable(MainNavItem.MainView.route) {
                AppScaffold(appNavController = appNavController, mainNavStartRoute = mainNavStartRoute)
            }
            composable(
                MainNavItem.DetailView.route.plus("/{${NavConstants.TYPE_KEY}}/{${NavConstants.ID_KEY}}"),
                arguments = listOf(
                    navArgument(NavConstants.ID_KEY) { type = NavType.IntType },
                    navArgument(NavConstants.TYPE_KEY) { type = NavType.EnumType(MediaViewType::class.java) }
                )
            ) { navBackStackEntry ->
                val args = navBackStackEntry.arguments
                val mediaType = args?.getSerializable(NavConstants.TYPE_KEY) as MediaViewType
                if (mediaType != MediaViewType.PERSON) {
                    MediaDetailView(
                        appNavController = appNavController,
                        itemId = args.getInt(NavConstants.ID_KEY),
                        type = mediaType
                    )
                } else {
                    PersonDetailView(
                        appNavController = appNavController,
                        personId = args.getInt(NavConstants.ID_KEY)
                    )
                }
            }
            composable(
                MainNavItem.SettingsView.route.plus("/{${NavConstants.SETTINGS_KEY}}"),
                arguments = listOf(
                    navArgument(NavConstants.SETTINGS_KEY) { type = NavType.StringType }
                )
            ) {
                val route = it.arguments?.getString(NavConstants.SETTINGS_KEY)
                SettingsTab(
                    appNavController = appNavController,
                    activity = this@MainActivity,
                    route = route
                )
            }
            composable(MainNavItem.SettingsView.route) {
                SettingsTab(appNavController = appNavController, activity = this@MainActivity)
            }
            composable(
                route = MainNavItem.SearchView.route.plus("/{${NavConstants.SEARCH_ID_KEY}}/{${NavConstants.SEARCH_TITLE_KEY}}"),
                arguments = listOf(
                    navArgument(NavConstants.SEARCH_ID_KEY) { type = NavType.IntType },
                    navArgument(NavConstants.SEARCH_TITLE_KEY) { type = NavType.StringType }
                )
            ) {
                it.arguments?.let { arguments ->
//                    val title = arguments.getString(NavConstants.SEARCH_TITLE_KEY) ?: ""
//                    val type = if (preferences.multiSearch) {
//                        MediaViewType.MIXED
//                    } else {
//                        MediaViewType[arguments.getInt(NavConstants.SEARCH_ID_KEY)]
//                    }

                    val (type, title) = if (preferences.multiSearch) {
                        Pair(MediaViewType.MIXED, "")
                    } else {
                        Pair(
                            MediaViewType[arguments.getInt(NavConstants.SEARCH_ID_KEY)],
                            arguments.getString(NavConstants.SEARCH_TITLE_KEY) ?: ""
                        )
                    }

                    SearchScreen(
                        appNavController = appNavController,
                        title = title,
                        mediaViewType = type
                    )
                }
            }
        }
    }

    @Composable
    private fun AppKeyboardFocusManager() {
        val context = LocalContext.current
        val focusManager = LocalFocusManager.current
        DisposableEffect(key1 = context) {
            val keyboardManager = KeyboardManager.getInstance(context)
            keyboardManager.attachKeyboardDismissListener {
                focusManager.clearFocus()
            }
            onDispose {
                keyboardManager.release()
            }
        }
    }
}