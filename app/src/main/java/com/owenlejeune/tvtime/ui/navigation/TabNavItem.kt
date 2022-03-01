package com.owenlejeune.tvtime.ui.navigation

import org.koin.core.component.KoinComponent

abstract class TabNavItem(val route: String): KoinComponent {
    abstract val name: String
}
