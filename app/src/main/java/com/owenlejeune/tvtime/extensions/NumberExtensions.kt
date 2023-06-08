package com.owenlejeune.tvtime.extensions

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Float.dpToPx(context: Context): Float {
    return this * context.resources.displayMetrics.density
}

@Composable
fun Int.toDp(): Dp = (this / LocalContext.current.resources.displayMetrics.density).toInt().dp
