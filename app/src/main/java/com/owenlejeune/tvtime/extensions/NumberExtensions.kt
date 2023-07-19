package com.owenlejeune.tvtime.extensions

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.pow

fun Float.dpToPx(context: Context): Float {
    return this * context.resources.displayMetrics.density
}

@Composable
fun Int.toDp(): Dp = (this / LocalContext.current.resources.displayMetrics.density).toInt().dp

fun Int.combineWith(other: Int): Int {
    val max = maxOf(this, other)
    val min = minOf(this, other)
    return 2f.pow(min).toInt() * ((2 * max) + 1)
}

fun Int.toCompositeParts(): Pair<Int, Int> {
    var a = 0
    var result = this
    do {
        result /= 2
        a++
    } while (result % 2 == 0)
    return pairOf(a, result/2)
}
