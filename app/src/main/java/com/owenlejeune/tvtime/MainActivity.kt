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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.owenlejeune.tvtime.ui.navigation.BottomNavItem
import com.owenlejeune.tvtime.ui.navigation.BottomNavigationRoutes
import com.owenlejeune.tvtime.ui.navigation.MainNavigationRoutes
import com.owenlejeune.tvtime.ui.theme.TVTimeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
        }
    }
}

@Composable
fun MyApp() {
    TVTimeTheme {

        val appNavController = rememberNavController()
        Box {
            MainNavigationRoutes(navController = appNavController)
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppView(appNavController: NavController) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val appBarTitle = remember { mutableStateOf(BottomNavItem.Items[0].name) }
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
            if (currentRoute in listOf(BottomNavItem.Movies.route, BottomNavItem.TV.route)) {
                SearchFab()
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            BottomNavigationRoutes(navController = navController)
        }
    }
}

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