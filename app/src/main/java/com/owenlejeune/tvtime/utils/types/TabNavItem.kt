package com.owenlejeune.tvtime.utils.types

import org.koin.core.component.KoinComponent

abstract class TabNavItem(val route: String): KoinComponent {
    abstract val name: String
}
