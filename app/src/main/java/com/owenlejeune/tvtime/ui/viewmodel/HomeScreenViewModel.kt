package com.owenlejeune.tvtime.ui.viewmodel

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class HomeScreenViewModel: ViewModel() {

    private object Backer {
        val appBarTitle = mutableStateOf("")
        val appBarActions = mutableStateOf<@Composable RowScope.() -> Unit>( {} )
        val fab = mutableStateOf<@Composable () -> Unit>( {} )
    }

    val appBarTitle = Backer.appBarTitle
    val appBarActions = Backer.appBarActions
    val fab = Backer.fab

}