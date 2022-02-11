package com.owenlejeune.tvtime.ui.screens.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.owenlejeune.tvtime.ui.components.PaletteView
import com.owenlejeune.tvtime.ui.components.TopLevelSwitch

@Composable
fun SettingsTab() {
    Column(modifier = Modifier.fillMaxSize()) {
        val showPalette = remember { mutableStateOf(false) }
        TopLevelSwitch(
            text = "Show Palette",
            onCheckChanged = { isChecked ->
                showPalette.value = isChecked
            }
        )
        if (showPalette.value) {
            PaletteView()
        }
    }
}