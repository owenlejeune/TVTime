package com.owenlejeune.tvtime.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val ColorScheme.actionButtonColor: Color
    @Composable
    get() = get(LightActionButton, DarkActionButton)

@Composable
private fun get(light: Color, dark: Color): Color {
    return if(isSystemInDarkTheme()) dark else light
}