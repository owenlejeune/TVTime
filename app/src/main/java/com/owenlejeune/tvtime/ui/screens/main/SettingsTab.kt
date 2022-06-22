package com.owenlejeune.tvtime.ui.screens.main

import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ResetTv
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kieronquinn.monetcompat.core.MonetCompat
import com.owenlejeune.tvtime.BuildConfig
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.preferences.AppPreferences
import com.owenlejeune.tvtime.ui.components.*
import com.owenlejeune.tvtime.utils.SessionManager
import dev.kdrag0n.monet.factory.ColorSchemeFactory
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.get

@Composable
fun SettingsTab(preferences: AppPreferences = get(AppPreferences::class.java)) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 8.dp, top = 8.dp, bottom = 8.dp, end = 24.dp)
            .verticalScroll(scrollState)
    ) {
//        val usePreferences = remember { mutableStateOf(preferences.usePreferences) }
//        TopLevelSwitch(
//            text = "Enable Preferences",
//            checkedState = usePreferences,
//            onCheckChanged = { isChecked ->
//                usePreferences.value = isChecked
//                preferences.usePreferences = isChecked
//            }
//        )
//
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .wrapContentHeight()
//                .padding(start = 8.dp, top = 8.dp, bottom = 8.dp, end = 24.dp)
//                .verticalScroll()
//        ) {
        PreferenceHeading(text = stringResource(R.string.preference_heading_search))

        val persistentSearch = remember { mutableStateOf(preferences.persistentSearch) }
        SwitchPreference(
            titleText = stringResource(R.string.preferences_persistent_search_title),
            subtitleText = stringResource(R.string.preferences_persistent_search_subtitle),
            checkState = persistentSearch.value,
            onCheckedChange = { isChecked ->
                persistentSearch.value = isChecked
                preferences.persistentSearch = isChecked
            }
        )

        val hideTitle = remember { mutableStateOf(preferences.hideTitle) }
        SwitchPreference(
            titleText = stringResource(R.string.preferences_hide_heading_title),
            subtitleText = stringResource(R.string.preferences_hide_heading_subtitle),
            checkState = hideTitle.value,
            onCheckedChange = { isChecked ->
                hideTitle.value = isChecked
                preferences.hideTitle = isChecked
            },
            enabled = persistentSearch.value
        )

        PreferenceHeading(text = stringResource(id = R.string.preference_heading_design))

        val isSystemColorsSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
        if (isSystemColorsSupported) {
            val useSystemColors = remember { mutableStateOf(preferences.useSystemColors) }
            SwitchPreference(
                titleText = stringResource(id = R.string.preference_system_colors_heading),
                checkState = useSystemColors.value,
                onCheckedChange = { isChecked ->
                    useSystemColors.value = isChecked
                    preferences.useSystemColors = isChecked
                    MonetCompat.useSystemColorsOnAndroid12 = isChecked
                    MonetCompat.getInstance().updateMonetColors()
                }
            )
        }

        SliderPreference(
            titleText = stringResource(id = R.string.preference_chroma_factor_heading),
            value = preferences.chromaMultiplier.toFloat() * 50f,
            onValueChangeFinished = { value ->
                with((value / 50f).toDouble()) {
                    preferences.chromaMultiplier = this
                    MonetCompat.chromaMultiplier = this
                }
                MonetCompat.getInstance().updateMonetColors()
            },
            enabled = if (isSystemColorsSupported) !preferences.useSystemColors else true
        )

        val showWallpaperPicker = remember { mutableStateOf(false) }
        val wallpaperPickerModifier = if (isSystemColorsSupported && preferences.useSystemColors) {
            Modifier
        } else {
            Modifier.clickable { showWallpaperPicker.value = true }
        }
        Text(
            text = stringResource(id = R.string.preference_wallpaper_color_heading),
            style = MaterialTheme.typography.titleLarge,
            fontSize = 20.sp,
            color = if (isSystemColorsSupported && preferences.useSystemColors) {
                MaterialTheme.colorScheme.outline
            } else {
                MaterialTheme.colorScheme.onBackground
            },
            modifier = wallpaperPickerModifier
                .padding(start = 8.dp)
        )
        if (showWallpaperPicker.value) {
            WallpaperPicker(showPopup = showWallpaperPicker)
        }

        if (BuildConfig.DEBUG) {
            Divider(modifier = Modifier.padding(start = 8.dp, top = 20.dp))
            Text(
                modifier = Modifier.padding(start = 8.dp, top = 20.dp),
                text = stringResource(R.string.preferences_debug_title),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            DebugOptions()
        }
    }
}

@Composable
private fun WallpaperPicker(
    showPopup: MutableState<Boolean>,
    preference: AppPreferences = get(AppPreferences::class.java)
) {
    val context = LocalContext.current

    val wallpaperColors = remember { mutableStateOf<List<Int>>(emptyList()) }
    val selectedWallpaperColor = remember { mutableStateOf(0) }
    LaunchedEffect(true) {
        val colors = MonetCompat.getInstance().getAvailableWallpaperColors() ?: emptyList()
        if (colors.isEmpty()) {
            Toast.makeText(context, context.getString(R.string.preference_no_wallpaper_colors), Toast.LENGTH_SHORT).show()
            showPopup.value = false

        }
        wallpaperColors.value = colors
        selectedWallpaperColor.value = MonetCompat.getInstance().getSelectedWallpaperColor() ?: 0
    }

    if (wallpaperColors.value.isNotEmpty() && selectedWallpaperColor.value != 0) {
        val colorCircleSize = 50.dp
        AlertDialog(
            modifier = Modifier.padding(12.dp),
            title = { Text(text = stringResource(id = R.string.preference_wallpaper_color_heading)) },
            text = {
                Row(
                    modifier = Modifier
                        .height(colorCircleSize)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    wallpaperColors.value.forEach {
                        CenteredIconCircle(
                            size = colorCircleSize,
                            backgroundColor = Color(it),
                            iconTint = Color.White,
                            icon = Icons.Filled.Check,
                            contentDescription = null,
                            showIcon = it == selectedWallpaperColor.value,
                            onClick = {
                                if (it != selectedWallpaperColor.value) {
                                    preference.selectedColor = it
                                    MonetCompat
                                        .getInstance()
                                        .updateMonetColors()
                                }
                            }
                        )
                    }

                    CenteredIconCircle(
                        size = colorCircleSize,
                        backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                        iconTint = MaterialTheme.colorScheme.onSecondaryContainer,
                        icon = Icons.Filled.RestartAlt,
                        contentDescription = null,
                        onClick = {
                            if (preference.selectedColor != Int.MAX_VALUE) {
                                preference.selectedColor = Int.MAX_VALUE
                                MonetCompat.getInstance().updateMonetColors()
                            } else {
                                Toast.makeText(context, context.getString(R.string.preference_wallpaper_color_default_selected), Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showPopup.value = false }) {
                    Text(text = stringResource(id = R.string.action_cancel))
                }
            },
            confirmButton = {},
            onDismissRequest = { showPopup.value = false }
        )
    }
}

@Composable
private fun DebugOptions(preferences: AppPreferences = get(AppPreferences::class.java)) {
    val context = LocalContext.current

    val firstLaunchTesting = remember { mutableStateOf(preferences.firstLaunchTesting) }
    SwitchPreference(
        titleText = "Always show onboarding flow",
        subtitleText = "Show onboarding flow on each launch",
        checkState = firstLaunchTesting.value,
        onCheckedChange = { isChecked ->
            firstLaunchTesting.value = isChecked
            preferences.firstLaunchTesting = isChecked
        }
    )

    val shouldShowPalette = remember { mutableStateOf(false) }
    Text(
        text = "Show material palette",
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 12.dp)
            .clickable(
                onClick = {
                    shouldShowPalette.value = true
                }
            )
    )
    if (shouldShowPalette.value) {
        AlertDialog(
            modifier = Modifier.padding(12.dp),
            title = { Text(text = "Palette") },
            text = { PaletteView() },
            dismissButton = {
                TextButton(onClick = { shouldShowPalette.value = false }) {
                    Text("Dismiss")
                }
            },
            confirmButton = {},
            onDismissRequest = { shouldShowPalette.value = false }
        )
    }

    Text(
        text = "Clear session",
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 12.dp)
            .clickable(
                onClick = {
                    preferences.guestSessionId = ""
                    SessionManager.clearSession {
                        Toast
                            .makeText(context, "Cleared session: $it", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            )
    )
}
