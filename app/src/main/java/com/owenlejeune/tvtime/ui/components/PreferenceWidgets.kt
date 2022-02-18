package com.owenlejeune.tvtime.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PreferenceHeading(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier
            .padding(8.dp),
        text = text,
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
fun SwitchPreference(
    titleText: String,
    checkState: Boolean,
    modifier: Modifier = Modifier,
    subtitleText: String = "",
    onCheckedChange: (Boolean) -> Unit = {},
    titleTextColor: Color = MaterialTheme.colorScheme.onBackground,
    subtitleTextColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    disabledTextColor: Color = MaterialTheme.colorScheme.outline,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(start = 8.dp, top = 8.dp, bottom = 8.dp, end = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1f)
        ) {
            val titleColor = if (enabled) titleTextColor else disabledTextColor
            val subtitleColor = if (enabled) subtitleTextColor else disabledTextColor
            Text(text = titleText, style = MaterialTheme.typography.titleLarge, color = titleColor, fontSize = 20.sp)
            if (subtitleText.isNotEmpty()) {
                Text(text = subtitleText, style = MaterialTheme.typography.bodyMedium, color = subtitleColor)
            }
        }

        CustomSwitch(
            modifier = Modifier
                .align(Alignment.CenterVertically),
            checked = checkState,
            onCheckedChange = onCheckedChange,
            width = 30.dp,
            height = 15.dp,
            colors = CustomSwitchColors.standardColors(),
            enabled = enabled
        )
    }
}

@Preview
@Composable
private fun SwitchPreferencePreview() {
    SwitchPreference("Title", true, subtitleText = "Subtitle")
}