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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PosterGrid(fetchMedia: (MutableState<List<MediaItem>>) -> Unit) {
    val mediaList = remember { mutableStateOf(emptyList<MediaItem>()) }
    fetchMedia(mediaList)

    LazyVerticalGrid(
        cells = GridCells.Fixed(count = 3),
        contentPadding = PaddingValues(8.dp)
    ) {
        listItems(mediaList.value) { item ->
            PosterItem(mediaItem = item)
        }
    }
}

@Composable
fun PosterItem(mediaItem: MediaItem) {
    val context = LocalContext.current
    val poster = TmdbUtils.getFullPosterPath(mediaItem)
    Image(
        painter = rememberImagePainter(
            data = poster,
            builder = {
                transformations(RoundedCornersTransformation(5f.dpToPx(context)))
                placeholder(R.drawable.placeholder)
            }
        ),
        contentDescription = mediaItem.title,
        modifier = Modifier
            .size(190.dp)
            .padding(5.dp)
            .clickable {
                Toast
                    .makeText(context, "${mediaItem.title} clicked", Toast.LENGTH_SHORT)
                    .show()
            }
    )
}

@Preview
@Composable
fun PosterItemPreview() {
    PosterItem(mediaItem = object : MediaItem(
        title = "Deadpool",
        posterPath = "/fSRb7vyIP8rQpL0I47P3qUsEKX3.jpg"
    ){})
}

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
fun CustomSwitch(
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

@Composable
fun PaletteView() {
    class PaletteItem(val color: Color, val textColor: Color, val text: String)

    val items = listOf(
        PaletteItem(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary, "primary"),
        PaletteItem(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer, "primary container"),
        PaletteItem(MaterialTheme.colorScheme.inversePrimary, Color.Black, "inverse primary"),
        PaletteItem(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.onSecondary, "secondary"),
        PaletteItem(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer, "secondary container"),
        PaletteItem(MaterialTheme.colorScheme.tertiary, MaterialTheme.colorScheme.onTertiary, "tertiary"),
        PaletteItem(MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.onTertiaryContainer, "tertiary container"),
        PaletteItem(MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.onBackground, "background"),
        PaletteItem(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.onSurface, "surface"),
        PaletteItem(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant, "surface variant"),
        PaletteItem(MaterialTheme.colorScheme.inverseSurface, MaterialTheme.colorScheme.inverseOnSurface, "inverse surface"),
        PaletteItem(MaterialTheme.colorScheme.error, MaterialTheme.colorScheme.onError, "error"),
        PaletteItem(MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.onErrorContainer, "primary"),
        PaletteItem(MaterialTheme.colorScheme.outline, Color.Black, "outline")
    )

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        listItems(items) { item ->
            PaletteItemView(
                surfaceColor = item.color,
                textColor = item.textColor,
                text = item.text
            )
        }
    }
}

@Composable
fun PaletteItemView(surfaceColor: Color, textColor: Color, text: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(12.dp),
        shape = RoundedCornerShape(30.dp),
        backgroundColor = surfaceColor
    ) {
        Text(
            text = text,
            color = textColor,
            modifier = Modifier.padding(30.dp, 12.dp)
        )
    }
}