package com.owenlejeune.tvtime.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.kieronquinn.monetcompat.core.MonetCompat
import com.owenlejeune.tvtime.preferences.AppPreferences
import com.owenlejeune.tvtime.ui.viewmodel.ApplicationViewModel
import org.koin.java.KoinJavaComponent.get

private val DarkColorPalette = darkColorScheme(
    primary = A1_200,
    onPrimary = A1_800,
    primaryContainer = A1_700,
    onPrimaryContainer = A1_100,
    inversePrimary = A1_600,
    secondary = A2_200,
    onSecondary = A2_800,
    secondaryContainer = A2_700,
    onSecondaryContainer = A2_100,
    tertiary = A3_200,
    onTertiary = A3_800,
    tertiaryContainer = A3_700,
    onTertiaryContainer = A3_100,
    error = ErrorDark,
    onError = OnErrorDark,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark,
    outline = N2_400,
    background = N1_900,
    onBackground = N1_100,
    surface = N1_900,
    onSurface = N1_100,
    surfaceVariant = N2_700,
    onSurfaceVariant = N2_200,
    inverseSurface = N1_100,
    inverseOnSurface = N1_900,
)

private val LightColorPalette = lightColorScheme(
    primary = A1_600,
    onPrimary = A1_0,
    primaryContainer = A1_100,
    onPrimaryContainer = A1_900,
    inversePrimary = A1_200,
    secondary = A2_600,
    onSecondary = A2_0,
    secondaryContainer = A2_100,
    onSecondaryContainer = A2_900,
    tertiary = A3_600,
    onTertiary = A3_0,
    tertiaryContainer = A3_100,
    onTertiaryContainer = A3_900,
    error = ErrorLight,
    onError = White,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnErrorContainerLight,
    outline = N2_500,
    background = N1_10,
    onBackground = N1_900,
    surface = N1_10,
    onSurface = N1_900,
    surfaceVariant = N2_100,
    onSurfaceVariant = N2_700,
    inverseSurface = N1_800,
    inverseOnSurface = N1_50
)

@Composable
fun TVTimeTheme(
    preferences: AppPreferences = get(AppPreferences::class.java),
    monetCompat: MonetCompat,
    content: @Composable () -> Unit
) {
    val isDarkTheme = when(preferences.darkTheme) {
        AppPreferences.DarkMode.Automatic.ordinal -> isSystemInDarkTheme()
        AppPreferences.DarkMode.Dark.ordinal -> true
        AppPreferences.DarkMode.Light.ordinal -> false
        else -> throw IllegalArgumentException("Illegal theme value ${preferences.darkTheme}")
    }

    val colors = when {
        isDarkTheme && preferences.useWallpaperColors -> monetCompat.darkMonetCompatScheme()
        isDarkTheme && !preferences.useWallpaperColors -> DarkColorPalette
        !isDarkTheme && preferences.useWallpaperColors -> monetCompat.lightMonetCompatScheme()
        !isDarkTheme && !preferences.useWallpaperColors -> LightColorPalette
        else -> throw Exception("Error getting theme colors, should never happen")
    }

    androidx.compose.material3.MaterialTheme(
        colorScheme = colors,
        typography = Typography
    ) {
        MaterialTheme(
            colors = Colors(
                primary = colors.primary,
                primaryVariant = colors.inversePrimary,
                secondary = colors.secondary,
                onSecondary = colors.onSecondary,
                secondaryVariant = colors.secondaryContainer,
                background = colors.background,
                onBackground = colors.onBackground,
                surface = colors.surface,
                error = colors.error,
                onPrimary = colors.onPrimary,
                onSurface = colors.onSurface,
                onError = colors.onError,
                isLight = !isDarkTheme
            ),
            shapes = Shapes,
            content = {
                content()

                val applicationViewModel = viewModel<ApplicationViewModel>()
                val statusBarColor by remember { applicationViewModel.statusBarColor }
                val navigationBarColor by remember { applicationViewModel.navigationBarColor }

                val systemUiController = rememberSystemUiController()
                systemUiController.setStatusBarColor(color = statusBarColor)
                systemUiController.setNavigationBarColor(color = Color.Transparent)
//                systemUiController.setNavigationBarColor(color = navigationBarColor)

                val view = LocalView.current
                if (!view.isInEditMode) {
                    SideEffect {
                        val window = (view.context as Activity).window
                        window.navigationBarColor = Color.Transparent.toArgb()

                        WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = isDarkTheme
                    }
                }
            }
        )
    }
}