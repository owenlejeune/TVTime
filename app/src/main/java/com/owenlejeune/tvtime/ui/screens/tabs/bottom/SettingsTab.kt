package com.owenlejeune.tvtime.ui.screens.tabs.bottom

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.owenlejeune.tvtime.preferences.AppPreferences
import com.owenlejeune.tvtime.ui.components.PaletteView
import com.owenlejeune.tvtime.ui.components.TopLevelSwitch
import org.koin.java.KoinJavaComponent.get

@Composable
fun SettingsTab(preferences: AppPreferences = get(AppPreferences::class.java)) {
    Column(modifier = Modifier.fillMaxSize()) {
        val usePreferences = remember { mutableStateOf(preferences.usePreferences) }
        TopLevelSwitch(
            text = "Enable Preferences",
            checkedState = usePreferences,
            onCheckChanged = { isChecked ->
                usePreferences.value = isChecked
                preferences.usePreferences = isChecked
            }
        )

        val shouldShowPalette = remember { mutableStateOf(false) }
        Text(
            text = "Show material palette",
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .padding(12.dp)
                .clickable(
                    enabled = usePreferences.value,
                    onClick = {
                        shouldShowPalette.value = true
                    }
                )
        )
        if (shouldShowPalette.value) {
            PaletteDialog(shouldShowPalette)
        }
    }
}

@Composable
private fun PaletteDialog(showDialog: MutableState<Boolean>) {
    AlertDialog(
        modifier = Modifier.padding(12.dp),
        title = {
            Text(text = "Palette")
        },
        text = {
            PaletteView()
        },
        dismissButton = {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { showDialog.value = false }
            ) {
                Text(text = "Dismiss", color = Color.White)
            }
        },
        confirmButton = {},
        onDismissRequest = { showDialog.value = false }
    )
}