package com.owenlejeune.tvtime.ui.screens.tabs.bottom

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Switch
import androidx.compose.material.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.owenlejeune.tvtime.BuildConfig
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.di.modules.preferencesModule
import com.owenlejeune.tvtime.preferences.AppPreferences
import com.owenlejeune.tvtime.ui.components.*
import com.owenlejeune.tvtime.utils.SessionManager
import org.koin.java.KoinJavaComponent.get

@Composable
fun SettingsTab(preferences: AppPreferences = get(AppPreferences::class.java)) {
    val scrollState = rememberScrollState()

    Column(modifier = Modifier
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

            if (BuildConfig.DEBUG) {
                Text(
                    modifier = Modifier.padding(start = 8.dp, top = 20.dp),
                    text = stringResource(R.string.preferences_debug_title),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                DebugOptions()
            }
        }
}

@Composable
private fun DebugOptions(preferences: AppPreferences = get(AppPreferences::class.java)) {
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
        PaletteDialog(shouldShowPalette)
    }

    Text(
        text = "Clear session",
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 12.dp)
            .clickable(
                onClick = {
                    preferences.guestSessionId = ""
                    SessionManager.clearSession()
                }
            )
    )
}

@Composable
private fun PaletteDialog(showDialog: MutableState<Boolean>) {
    AlertDialog(
        modifier = Modifier.padding(12.dp),
        title = { Text(text = "Palette") },
        text = { PaletteView() },
        dismissButton = {
            TextButton(onClick = { showDialog.value = false }) {
                Text("Dismiss")
            }
        },
        confirmButton = {},
        onDismissRequest = { showDialog.value = false }
    )
}