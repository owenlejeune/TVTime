package com.owenlejeune.tvtime.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.owenlejeune.tvtime.BuildConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TVTTopAppBar(
    title: @Composable () -> Unit,
    appNavController: NavController,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    TopAppBar(
        title = title,
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults
            .topAppBarColors(
                scrolledContainerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.primary
            ),
        navigationIcon = navigationIcon,
        actions = {
            if (BuildConfig.DEBUG) {
                NavStoredRouteButton(appNavController = appNavController)
                Spacer(modifier = Modifier.width(12.dp))
                StoreRouteButton()
                Spacer(modifier = Modifier.width(12.dp))
            }
            actions()
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TVTLargeTopAppBar(
    title: @Composable () -> Unit,
    appNavController: NavController,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    LargeTopAppBar(
        title = title,
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.largeTopAppBarColors(scrolledContainerColor = MaterialTheme.colorScheme.background),
        navigationIcon = navigationIcon,
        actions = {
            if (BuildConfig.DEBUG) {
                NavStoredRouteButton(appNavController = appNavController)
                Spacer(modifier = Modifier.width(12.dp))
                StoreRouteButton()
                Spacer(modifier = Modifier.width(12.dp))
            }
            actions()
        }
    )
}