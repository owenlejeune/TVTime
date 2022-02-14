package com.owenlejeune.tvtime.ui.screens.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.owenlejeune.tvtime.preferences.AppPreferences
import com.owenlejeune.tvtime.ui.components.PaletteView
import com.owenlejeune.tvtime.ui.components.TopLevelSwitch
import org.koin.java.KoinJavaComponent.get

@Composable
fun SettingsTab(preferences: AppPreferences = get(AppPreferences::class.java)) {
    Column(modifier = Modifier.fillMaxSize()) {
        val usePreferences = remember { mutableStateOf(preferences.usePreferences) }
        TopLevelSwitch(
            text = "Show Palette",
            checkedState = usePreferences,
            onCheckChanged = { isChecked ->
                usePreferences.value = isChecked
                preferences.usePreferences = isChecked
            }
        )
        if (usePreferences.value) {
            PaletteView()
        }
    }
}