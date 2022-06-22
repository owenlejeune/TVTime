package com.owenlejeune.tvtime

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.ColorFilter
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
import com.owenlejeune.tvtime.ui.components.RoundedTextField
import com.owenlejeune.tvtime.ui.components.SearchFab
import com.owenlejeune.tvtime.ui.navigation.BottomNavItem
import com.owenlejeune.tvtime.ui.navigation.MainNavGraph
import com.owenlejeune.tvtime.ui.navigation.MainNavItem
import com.owenlejeune.tvtime.ui.screens.main.MediaDetailView
import com.owenlejeune.tvtime.ui.screens.main.MediaViewType
import com.owenlejeune.tvtime.ui.screens.main.PersonDetailView
import com.owenlejeune.tvtime.ui.theme.TVTimeTheme
import com.owenlejeune.tvtime.utils.KeyboardManager
import com.owenlejeune.tvtime.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.get

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : MonetCompatActivity() {

    private val searchableScreens = listOf(BottomNavItem.Movies.route, BottomNavItem.TV.route, BottomNavItem.People.route)

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

        val focusRequester = remember { FocusRequester() }
        val focusSearchBar = rememberSaveable { mutableStateOf(false) }

        val appBarActions = remember { mutableStateOf<@Composable RowScope.() -> Unit>( {} ) }

        // todo - scroll state not remember when returing from detail screen

        Scaffold (
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                if (windowSize != WindowSizeClass.Expanded) {
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
        navigateToRoute(navController, item.route)
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
    private fun SearchBar(
        textState: MutableState<String>,
        placeholder: String
    ) {
        RoundedTextField(
            modifier = Modifier
                .padding(all = 12.dp)
                .height(35.dp),
            value = textState.value,
            onValueChange = { textState.value = it },
            placeHolder = stringResource(id = R.string.search_placeholder, placeholder),
            trailingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = stringResource(R.string.search_icon_content_descriptor),
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary)
                )
            }
        )
    }

    @Composable
    private fun MainContent(
        windowSize: WindowSizeClass,
        appNavController: NavHostController,
        navController: NavHostController,
        topBarScrollBehaviour: TopAppBarScrollBehavior,
        appBarTitle: MutableState<String>,
        appBarActions: MutableState<@Composable (RowScope.() -> Unit)> = mutableStateOf({}),
        mainNavStartRoute: String = BottomNavItem.Items[0].route
    ) {
        if (windowSize == WindowSizeClass.Expanded) {
            DualColumnMainContent(
                appNavController = appNavController,
                navController = navController,
                appBarTitle = appBarTitle,
                appBarActions = appBarActions,
                topBarScrollBehaviour = topBarScrollBehaviour,
                mainNavStartRoute = mainNavStartRoute
            )
        } else {
            SingleColumnMainContent(
                appNavController = appNavController,
                navController = navController,
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
        appBarTitle: MutableState<String>,
        appBarActions: MutableState<@Composable (RowScope.() -> Unit)> = mutableStateOf({}),
        mainNavStartRoute: String = BottomNavItem.Items[0].route
    ) {
        MainMediaView(
            appNavController = appNavController,
            navController = navController,
            appBarTitle = appBarTitle,
            appBarActions = appBarActions,
            mainNavStartRoute = mainNavStartRoute
        )
    }

    @Composable
    private fun DualColumnMainContent(
        appNavController: NavHostController,
        navController: NavHostController,
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
                    title = appBarTitle,
                    scrollBehavior = topBarScrollBehaviour,
                    appBarActions = appBarActions
                )
                MainMediaView(
                    appNavController = appNavController,
                    navController = navController,
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
        appBarTitle: MutableState<String>,
        appBarActions: MutableState<RowScope.() -> Unit> = mutableStateOf({}),
        mainNavStartRoute: String = BottomNavItem.Items[0].route
    ) {
        Column {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            if (currentRoute in searchableScreens) {
                val textState = remember { mutableStateOf("") }
                SearchBar(
                    textState,
                    appBarTitle.value
                )
            }

            MainNavGraph(
                appNavController = appNavController,
                navController = navController,
                appBarTitle = appBarTitle,
                appBarActions = appBarActions,
                startDestination = mainNavStartRoute
            )
        }
    }

    private object NavConstants {
        const val ID_KEY = "id_key"
        const val TYPE_KEY = "type_key"
    }

    @Composable
    private fun MainNavigationRoutes(
        startDestination: String = MainNavItem.MainView.route,
        mainNavStartRoute: String = BottomNavItem.Items[0].route,
        appNavController: NavHostController,
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