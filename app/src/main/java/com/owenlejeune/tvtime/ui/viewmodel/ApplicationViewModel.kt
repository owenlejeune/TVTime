package com.owenlejeune.tvtime.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.owenlejeune.tvtime.preferences.AppPreferences
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ApplicationViewModel: ViewModel(), KoinComponent {

    private val preferences: AppPreferences by inject()

    private object Backer: KoinComponent {
        private val preferences: AppPreferences by inject()

        val statusBarColor = mutableStateOf(Color.Transparent)
        val navigationBarColor = mutableStateOf(Color.Transparent)
        val currentRoute = mutableStateOf("")
        val storedRoute = mutableStateOf(preferences.storedTestRoute)
    }

    val statusBarColor = Backer.statusBarColor
    val navigationBarColor = Backer.navigationBarColor
    val currentRoute = Backer.currentRoute
    val storedRoute: State<String> = Backer.storedRoute

    fun setStoredRoute(route: String) {
        Backer.storedRoute.value = route
        preferences.storedTestRoute = route
    }

}