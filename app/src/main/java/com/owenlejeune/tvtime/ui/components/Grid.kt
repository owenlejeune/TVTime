package com.owenlejeune.tvtime.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.ceil

@Composable
private fun StaticGridInternal(
    columns: Int,
    itemCount: Int,
    modifier: Modifier = Modifier,
    horizontalSpacing: Dp = 4.dp,
    verticalSpacing: Dp = 4.dp,
    content: @Composable (Int) -> Unit
) {
    val numRows = ceil(itemCount.toFloat() / columns.toFloat()).toInt()
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(verticalSpacing)
    ) {
        for (i in 0 until numRows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(horizontalSpacing)
            ) {
                for (j in 0 until columns) {
                    if ((columns*i)+j < itemCount) {
                        content((columns*i)+j)
                    }
                }
            }
        }
    }
}

sealed class StaticGridCells {
    class Fixed(val count: Int): StaticGridCells()
    class Dynamic(val minSize: Dp): StaticGridCells()
}

@Composable
fun StaticGrid(
    cells: StaticGridCells,
    itemCount: Int,
    modifier: Modifier = Modifier,
    content: @Composable (Int) -> Unit
) {
    when (cells) {
        is StaticGridCells.Fixed -> {
            StaticGridInternal(
                columns = cells.count,
                itemCount = itemCount,
                verticalSpacing = 4.dp,
                horizontalSpacing = 4.dp,
                content = content
            )
        }
        is StaticGridCells.Dynamic -> {
            BoxWithConstraints(modifier = modifier) {
                val nColumns = maxOf((maxWidth / cells.minSize).toInt(), 1)
                val spacing = maxWidth - (cells.minSize * nColumns)
                StaticGridInternal(
                    columns = nColumns,
                    itemCount = itemCount,
                    verticalSpacing = 4.dp,
                    horizontalSpacing = spacing,
                    content = content
                )
            }
        }
    }
}