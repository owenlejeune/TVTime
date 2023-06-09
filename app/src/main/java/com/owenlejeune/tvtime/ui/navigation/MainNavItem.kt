package com.owenlejeune.tvtime.ui.navigation

sealed class MainNavItem(val route: String) {

    companion object {
        val Items = listOf(MainView, DetailView, SettingsView)
    }

    object MainView: MainNavItem("main_route")
    object DetailView: MainNavItem("detail_route")
    object SettingsView: MainNavItem("settings_route")
    object SearchView: MainNavItem("search_route")
    object WebLinkView: MainNavItem("web_link_route")
    object AccountView: MainNavItem("account_route")
    object AboutView: MainNavItem("about_route")

}