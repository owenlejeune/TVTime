package com.owenlejeune.tvtime.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.owenlejeune.tvtime.extensions.toDp

@SuppressLint("AutoboxingStateValueProperty")
@Composable
fun <T> PillSegmentedControl(
    items: List<T>,
    itemLabel: (index: Int, item: T) -> String,
    onItemSelected: (index: Int, item: T) -> Unit,
    modifier: Modifier = Modifier,
    defaultSelectedItemIndex: Int = 0,
    cornerRadius: Int = 50,
    colors: SegmentControlColors = SegmentControlDefaults.pillSegmentControlColors()
) {
    val selectedIndex = remember { mutableIntStateOf(defaultSelectedItemIndex) }

    val parentBoxSize = remember { mutableStateOf(IntSize.Zero) }
    val textViewSize = remember { mutableStateOf(IntSize.Zero) }
    val maxTextViewSize = remember { mutableStateOf(IntSize.Zero) }

    val offsetAnimation by animateDpAsState(targetValue = ((selectedIndex.value * textViewSize.value.width) + (2 * selectedIndex.value)).toDp(), label = "")

    BoxWithConstraints(
        modifier = modifier.then(
            Modifier
                .border(
                    width = 1.dp,
                    color = colors.borderColor(),
                    shape = RoundedCornerShape(cornerRadius)
                )
                .onGloballyPositioned {
                    parentBoxSize.value = it.size
                }
        )
    ) {
        Box(
            modifier = Modifier
                .offset(
                    x = offsetAnimation,
                    y = 0.dp
                )
                .clip(RoundedCornerShape(cornerRadius))
                .width((parentBoxSize.value.width / items.size).toDp())
                .height(parentBoxSize.value.height.toDp())
                .background(color = colors.pillColor())
        )

        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            items.forEachIndexed { index, item ->
                Text(
                    text = itemLabel(index, item),
                    color = if (selectedIndex.value == index) colors.selectedTextColor() else colors.textColor(),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .onGloballyPositioned {
                            textViewSize.value = it.size
                            if (it.size.width > maxTextViewSize.value.width) {
                                maxTextViewSize.value = it.size
                            }
                        }
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null
                        ) {
                            selectedIndex.value = index
                            onItemSelected(index, item)
                        }
                        .weight(1f)
                )
            }
        }
    }
}

interface SegmentControlColors {
    @Composable fun borderColor(): Color
    @Composable fun pillColor(): Color
    @Composable fun textColor(): Color
    @Composable fun selectedTextColor(): Color
}

object SegmentControlDefaults {
    @Composable
    fun pillSegmentControlColors(
        borderColor: Color = MaterialTheme.colorScheme.primary,
        pillColor: Color = MaterialTheme.colorScheme.primary,
        textColor: Color = MaterialTheme.colorScheme.primary,
        selectedTextColor: Color = MaterialTheme.colorScheme.background
    ): SegmentControlColors {
        return object : SegmentControlColors {
            @Composable override fun borderColor() = borderColor
            @Composable override fun pillColor() = pillColor
            @Composable override fun textColor() = textColor
            @Composable override fun selectedTextColor() = selectedTextColor
        }
    }
}

@Preview
@Composable
fun PillSegmentControlPreview() {
    val items = listOf("One", "Two", "Three", "Four")

    val colors = SegmentControlDefaults.pillSegmentControlColors(borderColor = Color.Red, pillColor = Color.Red, textColor = Color.Red, selectedTextColor = Color.White)

    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.background(Color.White)
    ) {
        PillSegmentedControl(items = items.subList(0, 2), itemLabel = { _, t -> t }, onItemSelected = { _, _ -> }, colors = colors)
        PillSegmentedControl(items = items.subList(0, 3), itemLabel = { _, t -> t }, onItemSelected = { _, _ -> }, colors = colors)
        PillSegmentedControl(items = items, itemLabel = { _, t -> t }, onItemSelected = { _, _ -> }, colors = colors)
    }
}