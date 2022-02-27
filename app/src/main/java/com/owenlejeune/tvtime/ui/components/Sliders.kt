package com.owenlejeune.tvtime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SliderWithLabel(
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChanged: (Float) -> Unit,
    sliderLabel: String,
    step: Int = 0,
    labelMinWidth: Dp = 24.dp
) {
    Column {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
        ) {

            val offset = getSliderOffset(
                value = value,
                valueRange = valueRange,
                boxWidth = maxWidth,
                labelWidth = labelMinWidth + 8.dp // Since we use a padding of 4.dp on either sides of the SliderLabel, we need to account for this in our calculation
            )

//            if (value > valueRange.start) {
                SliderLabel(
                    label = sliderLabel, minWidth = labelMinWidth, modifier = Modifier
                        .padding(start = offset)
                )
//            }
        }

        Slider(
            value = value,
            onValueChange = onValueChanged,
            valueRange = valueRange,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            steps = step,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary
            )
        )

    }
}


@Composable
fun SliderLabel(label: String, minWidth: Dp, modifier: Modifier = Modifier) {
    Text(
        label,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onPrimary,
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(4.dp)
            .defaultMinSize(minWidth = minWidth)
    )
}


private fun getSliderOffset(
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    boxWidth: Dp,
    labelWidth: Dp
): Dp {

    val coerced = value.coerceIn(valueRange.start, valueRange.endInclusive)
    val positionFraction = calcFraction(valueRange.start, valueRange.endInclusive, coerced)

    return (boxWidth - labelWidth) * positionFraction
}


// Calculate the 0..1 fraction that `pos` value represents between `a` and `b`
private fun calcFraction(a: Float, b: Float, pos: Float) =
    (if (b - a == 0f) 0f else (pos - a) / (b - a)).coerceIn(0f, 1f)