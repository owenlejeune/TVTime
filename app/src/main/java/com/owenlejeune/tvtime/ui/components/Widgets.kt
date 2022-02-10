package com.owenlejeune.tvtime.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.api.tmdb.TmdbUtils
import com.owenlejeune.tvtime.api.tmdb.model.MediaItem
import com.owenlejeune.tvtime.extensions.dpToPx
import com.owenlejeune.tvtime.extensions.listItems

@Composable
fun TopLevelSwitch(
    text: String,
    checkedState: MutableState<Boolean> = remember { mutableStateOf(false) },
    onCheckChanged: (Boolean) -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(12.dp),
        shape = RoundedCornerShape(30.dp),
        backgroundColor = when {
            isSystemInDarkTheme() && checkedState.value -> MaterialTheme.colorScheme.primary
            isSystemInDarkTheme() && !checkedState.value -> MaterialTheme.colorScheme.secondary
            checkedState.value -> MaterialTheme.colorScheme.primaryContainer
            else -> MaterialTheme.colorScheme.secondaryContainer
        }
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = text,
                color = when {
                    isSystemInDarkTheme() && checkedState.value -> MaterialTheme.colorScheme.onPrimary
                    isSystemInDarkTheme() && !checkedState.value -> MaterialTheme.colorScheme.onSecondary
                    checkedState.value -> MaterialTheme.colorScheme.onPrimaryContainer
                    else -> MaterialTheme.colorScheme.onSecondaryContainer
                },
                modifier = Modifier.padding(30.dp, 12.dp),
                fontSize = 18.sp
            )
            CustomSwitch(
                modifier = Modifier.padding(40.dp, 12.dp),
                onCheckChanged = { isChecked ->
                    checkedState.value = isChecked
                    onCheckChanged(isChecked)
                }
            )
        }
    }
}

@Composable
private fun CustomSwitch(
    modifier: Modifier = Modifier,
    switchState: MutableState<Boolean> = remember { mutableStateOf(false) },
    onCheckChanged: (Boolean) -> Unit = {}
) {
    val width = 30.dp
    val height = 15.dp
    val gapBetweenThumbAndTrackEdge = 2.dp

    val thumbRadius = (height / 2) - gapBetweenThumbAndTrackEdge
    val animatePosition = animateFloatAsState(
        targetValue = if (switchState.value) {
            with(LocalDensity.current) { (width - thumbRadius - gapBetweenThumbAndTrackEdge).toPx() }
        } else {
            with (LocalDensity.current) { (thumbRadius + gapBetweenThumbAndTrackEdge).toPx() }
        }
    )

    val uncheckedTrackColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.outline
    val uncheckedThumbColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.surfaceVariant
    val checkedTrackColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.primary
    val checkedThumbColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer

    Canvas(
        modifier = modifier
            .size(width = width, height = height)
            .scale(scale = 2f)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        switchState.value = !switchState.value
                        onCheckChanged(switchState.value)
                    }
                )
            }
    ) {
        drawRoundRect(
            color = if (switchState.value) checkedTrackColor else uncheckedTrackColor,
            cornerRadius = CornerRadius(x = 10.dp.toPx(), y = 10.dp.toPx())
        )
        drawCircle(
            color = if (switchState.value) checkedThumbColor else uncheckedThumbColor,
            radius = thumbRadius.toPx(),
            center = Offset(
                x = animatePosition.value,
                y = size.height / 2
            )
        )
    }
}

@Preview(name = "TopLevelSwitch", showBackground = true)
@Preview(name = "Dark TopLevelSwitch", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun TopLevelSwitchPreview() {
    val context = LocalContext.current
    TopLevelSwitch("This is a switch") { isChecked ->
        Toast.makeText(context, "Switch changed to $isChecked", Toast.LENGTH_SHORT).show()
    }
}