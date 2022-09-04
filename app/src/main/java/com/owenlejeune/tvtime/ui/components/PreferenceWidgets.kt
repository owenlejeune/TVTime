package com.owenlejeune.tvtime.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
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

@Composable
fun SliderPreference(
    modifier: Modifier = Modifier,
    titleText: String,
    value: Float,
    onValueChangeFinished: (Float) -> Unit ,
    titleTextColor: Color = MaterialTheme.colorScheme.onBackground,
    disabledTextColor: Color = MaterialTheme.colorScheme.outline,
    enabled: Boolean = true
) {
    Column(
        modifier = modifier
            .wrapContentHeight()
            .padding(all = 8.dp)
    ) {
        val titleColor = if (enabled) titleTextColor else disabledTextColor
        Text(text = titleText, style = MaterialTheme.typography.titleLarge, color = titleColor, fontSize = 20.sp)

        var sliderValue by remember { mutableStateOf(value) }

        Slider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp),
            value = sliderValue,
            onValueChange = { sliderValue = it },
            enabled = enabled,
            valueRange = 0f..100f,
            onValueChangeFinished = {
                onValueChangeFinished(sliderValue)
            },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadioButtonPreference(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selected: Boolean,
    onClick: () -> Unit,
    title: String,
    icon: ImageVector,
    titleTextColor: Color = MaterialTheme.colorScheme.onBackground,
    disabledTextColor: Color = MaterialTheme.colorScheme.outline
) {
    Row(
//        modifier = modifier
//            .clickable(
//                enabled = enabled,
//                onClick = onClick
//            )
//            .semantics { role = Role.RadioButton }
        modifier = modifier
            .padding(all = 8.dp)
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            )
    ) {
        Icon(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(end = 16.dp),
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary
        )

        val titleColor = if (enabled) titleTextColor else disabledTextColor
        Text(
            modifier = Modifier
                .align(Alignment.CenterVertically),
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = titleColor,
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.weight(1f))

        RadioButton(
            modifier = Modifier.align(Alignment.CenterVertically),
            selected = selected,
            onClick = null
        )
    }
}