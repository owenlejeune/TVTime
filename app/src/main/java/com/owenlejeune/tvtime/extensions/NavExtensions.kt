package com.owenlejeune.tvtime.extensions

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination

fun NavController.navigateInBottomBar(route: String) = navigate(route) {
    popUpTo(graph.findStartDestination().id) {
        inclusive = true
    }
    launchSingleTop = true
    restoreState = true
}