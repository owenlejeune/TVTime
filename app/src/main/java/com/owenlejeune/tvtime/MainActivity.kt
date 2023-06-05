package com.owenlejeune.tvtime

import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
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
import androidx.navigation.navDeepLink
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.kieronquinn.monetcompat.app.MonetCompatActivity
import com.owenlejeune.tvtime.extensions.WindowSizeClass
import com.owenlejeune.tvtime.extensions.navigateInBottomBar
import com.owenlejeune.tvtime.extensions.rememberWindowSizeClass
import com.owenlejeune.tvtime.preferences.AppPreferences
import com.owenlejeune.tvtime.ui.components.AccountIcon
import com.owenlejeune.tvtime.ui.components.ProfileMenuContainer
import com.owenlejeune.tvtime.ui.components.ProfileMenuDefaults
import com.owenlejeune.tvtime.ui.components.ProfileMenuOverlay
import com.owenlejeune.tvtime.ui.navigation.BottomNavItem
import com.owenlejeune.tvtime.ui.navigation.MainNavGraph
import com.owenlejeune.tvtime.ui.navigation.MainNavItem
import com.owenlejeune.tvtime.ui.screens.SearchScreen
import com.owenlejeune.tvtime.ui.screens.main.*
import com.owenlejeune.tvtime.ui.theme.TVTimeTheme
import com.owenlejeune.tvtime.utils.KeyboardManager
import com.owenlejeune.tvtime.utils.NavConstants
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

        val mainNavStartRoute = BottomNavItem.SortedItems[0].route

        lifecycleScope.launchWhenCreated {
            monet.awaitMonetReady()
            setContent {
                AppKeyboardFocusManager()
                TVTimeTheme(monetCompat = monet) {
                    val windowSize = rememberWindowSizeClass()
                    val appNavController = rememberNavController()
                    Box {
                       MainNavigationRoutes(
                           appNavController = appNavController,
                           mainNavStartRoute = mainNavStartRoute,
                           windowSize = windowSize
                       )
                    }
                }
            }
        }
    }

    @Composable
    private fun AppScaffold(
        appNavController: NavHostController,
        mainNavStartRoute: String = BottomNavItem.SortedItems[0].route,
        windowSize: WindowSizeClass,
        preferences: AppPreferences = get(AppPreferences::class.java)
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
                            appNavController = appNavController,
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
        appNavController: NavHostController,
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
        preferences: AppPreferences = get(AppPreferences::class.java)
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        NavigationBar {
            BottomNavItem.SortedItems.forEach { item ->
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
        mainNavStartRoute: String = BottomNavItem.SortedItems[0].route
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
        mainNavStartRoute: String = BottomNavItem.SortedItems[0].route
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
        mainNavStartRoute: String = BottomNavItem.SortedItems[0].route,
        preferences: AppPreferences = get(AppPreferences::class.java)
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        Row(modifier = Modifier.fillMaxSize()) {
            NavigationRail {
                Spacer(modifier = Modifier.weight(1f))
                BottomNavItem.SortedItems.forEachIndexed { index, item ->
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
                    if (index < BottomNavItem.SortedItems.size - 1) {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
            }
            Column {
                TopBar(
                    appNavController = appNavController,
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
        mainNavStartRoute: String = BottomNavItem.SortedItems[0].route
    ) {
        Column {
            BackHandler(enabled = true) {
                finish()
            }

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

    @Composable
    private fun MainNavigationRoutes(
        startDestination: String = MainNavItem.MainView.route,
        mainNavStartRoute: String = MainNavItem.Items[0].route,
        appNavController: NavHostController,
        windowSize: WindowSizeClass,
        preferences: AppPreferences = get(AppPreferences::class.java)
    ) {
        NavHost(navController = appNavController, startDestination = startDestination) {
            composable(MainNavItem.MainView.route) {
                AppScaffold(
                    appNavController = appNavController,
                    mainNavStartRoute = mainNavStartRoute,
                    windowSize = windowSize
                )
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

                when (mediaType) {
                    MediaViewType.PERSON -> {
                        PersonDetailView(
                            appNavController = appNavController,
                            personId = args.getInt(NavConstants.ID_KEY)
                        )
                    }
                    MediaViewType.LIST -> {
                        ListDetailView(
                            appNavController = appNavController,
                            itemId = args.getInt(NavConstants.ID_KEY),
                            windowSize = windowSize
                        )
                    }
                    else -> {
                        MediaDetailView(
                            appNavController = appNavController,
                            itemId = args.getInt(NavConstants.ID_KEY),
                            type = mediaType,
                            windowSize = windowSize
                        )
                    }
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
                    val (type, title) = if (preferences.multiSearch) {
                        Pair(
                            MediaViewType.MIXED,
                            stringResource(id = R.string.search_all_title)
                        )
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
            composable(
                route = MainNavItem.WebLinkView.route.plus("/{${NavConstants.WEB_LINK_KEY}}"),
                arguments = listOf(
                    navArgument(NavConstants.WEB_LINK_KEY) { type = NavType.StringType }
                )
            ) {
                val url = it.arguments?.getString(NavConstants.WEB_LINK_KEY)
                url?.let { 
                    WebLinkView(url = url, appNavController = appNavController)
                }
            }
            composable(
                route = MainNavItem.AccountView.route,
                deepLinks = listOf(
                    navDeepLink { uriPattern = "app://tvtime.auth.{${NavConstants.ACCOUNT_KEY}}" }
                )
            ) {
                val deepLink = it.arguments?.getString(NavConstants.ACCOUNT_KEY)
                AccountView(
                    appNavController = appNavController,
                    doSignInPartTwo = deepLink == NavConstants.AUTH_REDIRECT_PAGE
                )
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