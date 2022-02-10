package com.owenlejeune.tvtime.ui.navigation

sealed class MainNavItem(val route: String) {

    object MainView: MainNavItem("main_route")
    object DetailView: MainNavItem("detail_route")

}