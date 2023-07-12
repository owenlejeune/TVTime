package com.owenlejeune.tvtime.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel

class ApplicationViewModel: ViewModel() {

    private object Backer {
        val statusBarColor = mutableStateOf(Color.Transparent)
        val navigationBarColor = mutableStateOf(Color.Transparent)
    }

    val statusBarColor = Backer.statusBarColor
    val navigationBarColor = Backer.navigationBarColor

}