package com.owenlejeune.tvtime.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.magnifier
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.ThresholdConfig
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.owenlejeune.tvtime.R
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentCard(
    modifier: Modifier = Modifier,
    title: String? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    textColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    content: @Composable () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            title?.let {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 16.dp, top = 12.dp),
                    color = textColor
                )
            }
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandableContentCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    title: @Composable () -> Unit = {},
    collapsedText: String = stringResource(id = R.string.expandable_see_more),
    expandedText: String = stringResource(id = R.string.expandable_see_less),
    toggleTextColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    content: @Composable (Boolean) -> Unit = {}
) {
    var expandedState by remember { mutableStateOf(false) }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column {
            title()
            Column(
                modifier = Modifier
                    .animateContentSize(
                        animationSpec = tween(
                            durationMillis = 300,
                            easing = LinearOutSlowInEasing
                        )
                    )
            ) {
                content(expandedState)
            }
            Text(
                text = if (expandedState) expandedText else collapsedText,
                color = toggleTextColor,
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clickable(
                        onClick = {
                            expandedState = !expandedState
                        }
                    )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListContentCard(
    modifier: Modifier = Modifier,
    header: @Composable (() -> Unit)? = null,
    footer: @Composable (() -> Unit)? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            header?.invoke()
            Column(
                content = content,
                modifier = Modifier
                    .fillMaxWidth()
            )
            footer?.invoke()
        }
    }
}

@Composable
fun TwoLineImageTextCard(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    imageUrl: String? = null,
    placeholder: ImageVector = Icons.Filled.Person,
    titleTextColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    subtitleTextColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onItemClicked: () -> Unit = {}
) {
    Column(modifier = modifier) {
        PosterItem(
            width = 120.dp,
            onClick = onItemClicked,
            url = imageUrl,
            placeholder = placeholder,
            title = title,
            elevation = 0.dp,
            overrideShowTitle = false
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp),
            text = title,
            minLines = 2,
            maxLines = 2,
            color = titleTextColor,
            style = MaterialTheme.typography.bodyMedium,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 2,
            text = subtitle ?: "",
            style = MaterialTheme.typography.bodySmall,
            color = subtitleTextColor,
            overflow = TextOverflow.Ellipsis
        )
//        MinLinesText(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(top = 5.dp),
//            minLines = 2,
//            text = title,
//            color = titleTextColor,
//            style = MaterialTheme.typography.bodyMedium
//        )
//        subtitle?.let {
//            MinLinesText(
//                modifier = Modifier.fillMaxWidth(),
//                minLines = 2,
//                text = subtitle,
//                style = MaterialTheme.typography.bodySmall,
//                color = subtitleTextColor
//            )
//        }
    }
}