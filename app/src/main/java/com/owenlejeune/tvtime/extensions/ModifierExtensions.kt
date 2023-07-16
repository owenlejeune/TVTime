package com.owenlejeune.tvtime.extensions

import android.view.View
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalView

fun Modifier.shimmerBackground(
    shape: Shape = RectangleShape,
    tint: Color = Color.LightGray
): Modifier = composed {
    val transition = rememberInfiniteTransition()
    val translateAnimation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 400f,
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 1500, easing = LinearOutSlowInEasing),
            RepeatMode.Restart
        ),
    )
    val shimmerColors = listOf(
        tint.copy(alpha = 0.9f),
        tint.copy(alpha = 0.4f),
    )
    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnimation, translateAnimation),
        end = Offset(translateAnimation + 100f, translateAnimation + 100f),
        tileMode = TileMode.Mirror,
    )
    return@composed this.then(background(brush, shape))
}

fun Modifier.combinedOnVisibilityChange(
    onVisible: () -> Unit,
    onNotVisible: () -> Unit
): Modifier = composed {
    val view = LocalView.current
    var isVisible by remember { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(isVisible) {
        when (isVisible) {
            true -> onVisible()
            false -> onNotVisible()
            else -> {}
        }
    }

    onGloballyPositioned { coordinates ->
        isVisible = coordinates.isCompletelyVisible(view)
    }
}

fun LayoutCoordinates.isCompletelyVisible(view: View): Boolean {
    if (!isAttached) return false
    // Window relative bounds of our compose root view that are visible on the screen
    val globalRootRect = android.graphics.Rect()
    if (!view.getGlobalVisibleRect(globalRootRect)) {
        // we aren't visible at all.
        return false
    }
    val bounds = boundsInWindow()
    // Make sure we are completely in bounds.
    return bounds.top >= globalRootRect.top &&
            bounds.left >= globalRootRect.left &&
            bounds.right <= globalRootRect.right &&
            bounds.bottom <= globalRootRect.bottom
}