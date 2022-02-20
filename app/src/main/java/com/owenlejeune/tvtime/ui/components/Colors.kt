package com.owenlejeune.tvtime.ui.components

import androidx.compose.material.ContentAlpha
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver


class CustomSwitchColors private constructor(
    val lightUncheckedTrackColor: Color,
    val darkUncheckedTrackColor: Color,
    val lightUncheckedThumbColor: Color,
    val darkUncheckedThumbColor: Color,
    val lightCheckedTrackColor: Color,
    val darkCheckedTrackColor: Color,
    val lightCheckedThumbColor: Color,
    val darkCheckedThumbColor: Color,
    val lightDisabledTrackColor: Color,
    val darkDisabledTrackColor: Color,
    val lightDisabledThumbColor: Color,
    val darkDisabledThumbColor: Color
){
    companion object {
        @Composable
        fun topLevelColors(
            lightUncheckedTrackColor: Color = MaterialTheme.colorScheme.outline,
            darkUncheckedTrackColor: Color = MaterialTheme.colorScheme.surfaceVariant,
            lightUncheckedThumbColor: Color = MaterialTheme.colorScheme.surfaceVariant,
            darkUncheckedThumbColor: Color = MaterialTheme.colorScheme.outline,
            lightCheckedTrackColor: Color = MaterialTheme.colorScheme.primary,
            darkCheckedTrackColor: Color = MaterialTheme.colorScheme.outline,
            lightCheckedThumbColor: Color = MaterialTheme.colorScheme.primaryContainer,
            darkCheckedThumbColor: Color = MaterialTheme.colorScheme.primary,
            lightDisabledTrackColor: Color = lightUncheckedTrackColor
                .copy(alpha = ContentAlpha.disabled)
                .compositeOver(MaterialTheme.colorScheme.surface),
            darkDisabledTrackColor: Color = darkUncheckedTrackColor
                .copy(alpha = ContentAlpha.disabled)
                .compositeOver(MaterialTheme.colorScheme.surface),
            lightDisabledThumbColor: Color = lightUncheckedThumbColor
                .copy(alpha = ContentAlpha.disabled)
                .compositeOver(MaterialTheme.colorScheme.surface),
            darkDisabledThumbColor: Color = darkUncheckedThumbColor
                .copy(alpha = ContentAlpha.disabled)
                .compositeOver(MaterialTheme.colorScheme.surface)
        ): CustomSwitchColors {
            return CustomSwitchColors(
                lightUncheckedTrackColor,
                darkUncheckedTrackColor,
                lightUncheckedThumbColor,
                darkUncheckedThumbColor,
                lightCheckedTrackColor,
                darkCheckedTrackColor,
                lightCheckedThumbColor,
                darkCheckedThumbColor,
                lightDisabledTrackColor,
                darkDisabledTrackColor,
                lightDisabledThumbColor,
                darkDisabledThumbColor
            )
        }

        @Composable
        fun standardColors(
            lightUncheckedTrackColor: Color = MaterialTheme.colorScheme.outline,
            darkUncheckedTrackColor: Color = MaterialTheme.colorScheme.surfaceVariant,
            lightUncheckedThumbColor: Color = MaterialTheme.colorScheme.surfaceVariant,
            darkUncheckedThumbColor: Color = MaterialTheme.colorScheme.outline,
            lightCheckedTrackColor: Color = MaterialTheme.colorScheme.primary,
            darkCheckedTrackColor: Color = MaterialTheme.colorScheme.secondaryContainer,
            lightCheckedThumbColor: Color = MaterialTheme.colorScheme.primaryContainer,
            darkCheckedThumbColor: Color = MaterialTheme.colorScheme.primary,
            lightDisabledTrackColor: Color = lightUncheckedTrackColor
                .copy(alpha = ContentAlpha.disabled)
                .compositeOver(MaterialTheme.colorScheme.surface),
            darkDisabledTrackColor: Color = darkUncheckedTrackColor
                .copy(alpha = ContentAlpha.disabled)
                .compositeOver(MaterialTheme.colorScheme.surface),
            lightDisabledThumbColor: Color = lightUncheckedThumbColor
                .copy(alpha = ContentAlpha.disabled)
                .compositeOver(MaterialTheme.colorScheme.surface),
            darkDisabledThumbColor: Color = darkUncheckedThumbColor
                .copy(alpha = ContentAlpha.disabled)
                .compositeOver(MaterialTheme.colorScheme.surface)
        ): CustomSwitchColors {
            return CustomSwitchColors(
                lightUncheckedTrackColor,
                darkUncheckedTrackColor,
                lightUncheckedThumbColor,
                darkUncheckedThumbColor,
                lightCheckedTrackColor,
                darkCheckedTrackColor,
                lightCheckedThumbColor,
                darkCheckedThumbColor,
                lightDisabledTrackColor,
                darkDisabledTrackColor,
                lightDisabledThumbColor,
                darkDisabledThumbColor
            )
        }
    }
}