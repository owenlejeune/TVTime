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
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
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
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp),
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
        modifier = modifier
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            ),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            title()
            content(expandedState)
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
fun LazyListContentCard(
    modifier: Modifier = Modifier,
    header: @Composable (() -> Unit)? = null,
    footer: @Composable (() -> Unit)? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    content: LazyListScope.() -> Unit
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
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            header?.invoke()
            val listState = rememberLazyListState()
            LazyColumn(
                content = content,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                state = listState
            )
            footer?.invoke()
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
    noDataImage: Int = R.drawable.placeholder,
    placeholder: Int = R.drawable.placeholder,
    titleTextColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    subtitleTextColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onItemClicked: () -> Unit = {}
) {
    Column(modifier = modifier) {
        PosterItem(
            width = 120.dp,
            onClick = onItemClicked,
            url = imageUrl,
            noDataImage = noDataImage,
            placeholder = placeholder,
            title = title,
            elevation = 0.dp,
            overrideShowTitle = false
        )
        MinLinesText(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp),
            minLines = 2,
            text = title,
            color = titleTextColor,
            style = MaterialTheme.typography.bodyMedium
        )
        subtitle?.let {
            MinLinesText(
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = subtitleTextColor
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeableActionCard(
    modifier: Modifier = Modifier,
    leftSwipeCard: (@Composable () -> Unit)? = null,
    rightSwipeCard: (@Composable () -> Unit)? = null,
    leftSwiped: () -> Unit = {},
    rightSwiped: () -> Unit = {},
    animationSpec: AnimationSpec<Float> = tween(250),
    velocityThreshold: Dp = 125.dp,
//    shape: Shape = RectangleShape,
    mainContent: @Composable () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    val thresholds = { _: SwipeCardState, _: SwipeCardState ->
        FractionalThreshold(0.6f)
    }

    Box(
        modifier = modifier//.clip(shape)
    ) {
        val swipeableState = rememberSwipeableState(
            initialValue = SwipeCardState.DEFAULT,
            animationSpec = animationSpec
        )

        val swipeLeftCardVisible = remember { mutableStateOf(false) }
        val swipeEnabled = remember { mutableStateOf(true) }
        val maxWidthInPx = with(LocalDensity.current) {
            LocalConfiguration.current.screenWidthDp.dp.toPx()
        }

        val anchors = hashMapOf(0f to SwipeCardState.DEFAULT)
        leftSwipeCard?.let { anchors[-maxWidthInPx] = SwipeCardState.LEFT }
        rightSwipeCard?.let { anchors[maxWidthInPx] = SwipeCardState.RIGHT }

        Surface(
            color = Color.Transparent,
            content = if (swipeLeftCardVisible.value) {
                leftSwipeCard
            } else {
                rightSwipeCard
            } ?: {}
        )

        Surface(
            color = Color.Transparent,
            modifier = Modifier
                .fillMaxWidth()
                .offset {
                    var offset = swipeableState.offset.value.roundToInt()
                    if (offset < 0 && leftSwipeCard == null) offset = 0
                    if (offset > 0 && rightSwipeCard == null) offset = 0
                    IntOffset(offset, 0)
                }
                .swipeable(
                    state = swipeableState,
                    anchors = anchors,
                    orientation = Orientation.Horizontal,
                    enabled = swipeEnabled.value,
                    thresholds = thresholds,
                    velocityThreshold = velocityThreshold
                )
        ) {
            val resetStateToDefault = {
                coroutineScope.launch {
                    swipeEnabled.value = false
                    swipeableState.animateTo(SwipeCardState.DEFAULT)
                    swipeEnabled.value = true
                }
            }
            when {
                swipeableState.currentValue == SwipeCardState.LEFT && !swipeableState.isAnimationRunning -> {
                    leftSwiped()
                    LaunchedEffect(Unit) {
                        resetStateToDefault()
                    }
                }
                swipeableState.currentValue == SwipeCardState.RIGHT && !swipeableState.isAnimationRunning -> {
                    rightSwiped()
                    LaunchedEffect(Unit) {
                        resetStateToDefault()
                    }
                }
            }

            swipeLeftCardVisible.value = swipeableState.offset.value <= 0

            mainContent()
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DraggableCard(
    modifier: Modifier = Modifier,
    cardOffset: Float,
    isRevealed: Boolean,
    cardElevation: CardElevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    shape: Shape = RectangleShape,
    onExpand: () -> Unit = {},
    onCollapse: () -> Unit = {},
    backgroundContent: @Composable () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    val animationDuration = 500
    val velicityThreshold = 125.dp

    val thresholds: (from: SwipeCardState, to: SwipeCardState) -> ThresholdConfig = { _, _ ->
        FractionalThreshold(0.6f)
    }

    val swipeableState = rememberSwipeableState(
        initialValue = SwipeCardState.DEFAULT,
        animationSpec = tween(animationDuration)
    )

    val maxWidthPx = with(LocalDensity.current) {
        LocalConfiguration.current.screenWidthDp.dp.toPx()
    }

    val anchors = hashMapOf(0f to SwipeCardState.DEFAULT)
    anchors[-maxWidthPx] = SwipeCardState.LEFT

    val swipeEnabled = remember { mutableStateOf(true) }

    Box (
        modifier = Modifier.clip(shape = shape)
    ) {
        backgroundContent()

        Card(
            shape = shape,
            elevation = cardElevation,
            modifier = modifier
                .background(color = backgroundColor)
                .offset {
                    var offset = swipeableState.offset.value.roundToInt()
                    if (offset < 0) offset = 0
                    IntOffset(offset, 0)
                }
                .swipeable(
                    state = swipeableState,
                    anchors = anchors,
                    orientation = Orientation.Horizontal,
                    enabled = swipeEnabled.value,
                    thresholds = thresholds,
                    velocityThreshold = velicityThreshold
                )
        ) {
            if (swipeableState.currentValue == SwipeCardState.LEFT && !swipeableState.isAnimationRunning) {
                onExpand()
                LaunchedEffect(key1 = Unit) {
                    coroutineScope.launch {
                        swipeEnabled.value = false
                        swipeableState.animateTo(SwipeCardState.DEFAULT)
                        swipeEnabled.value = true
                    }
                }
            }
            content()
        }
//        DraggableCardInternal(
//            modifier = modifier,
//            cardOffset = cardOffset,
//            isRevealed = isRevealed,
//            cardElevation = cardElevation,
//            backgroundColor = backgroundColor,
//            shape = shape,
//            onExpand = onExpand,
//            onCollapse = onCollapse,
//            content = content
//        )
    }
}

private enum class SwipeCardState {
    DEFAULT,
    LEFT,
    RIGHT
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun DraggableCardInternal(
    modifier: Modifier = Modifier,
    cardOffset: Float,
    isRevealed: Boolean,
    cardElevation: CardElevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    shape: Shape = RectangleShape,
    onExpand: () -> Unit = {},
    onCollapse: () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    val animationDuration = 500

    val swipeableState = rememberSwipeableState(
        initialValue = SwipeCardState.DEFAULT,
        animationSpec = tween(animationDuration)
    )

    val maxWidthPx = with(LocalDensity.current) {
        LocalConfiguration.current.screenWidthDp.dp.toPx()
    }

    val anchors = hashMapOf(0f to SwipeCardState.DEFAULT)
    anchors[-maxWidthPx] = SwipeCardState.LEFT

    Card(
        modifier = modifier
            .background(color = backgroundColor),
        shape = shape,
        elevation = cardElevation
    ){}
//    val animationDuration = 500
//    val minDragAmount = 5
//
//    val transitionState = remember {
//        MutableTransitionState(isRevealed).apply {
//            targetState = !isRevealed
//        }
//    }
//    val transition = updateTransition(targetState = transitionState, "cardTransition")
//    val offsetTransition by transition.animateFloat(
//        label = "cardOffsetTransition",
//        transitionSpec = { tween(durationMillis = animationDuration) },
//        targetValueByState = { if (isRevealed) cardOffset else 0f },
//    )
//
//    Card(
//        modifier = modifier
//            .background(color = backgroundColor)
//            .offset { IntOffset(offsetTransition.roundToInt(), 0) }
//            .pointerInput(Unit) {
//                detectHorizontalDragGestures { _, dragAmount ->
//                    when {
//                        dragAmount >= minDragAmount -> onExpand()
//                        dragAmount < -minDragAmount -> onCollapse()
//                    }
//                }
//            },
//        shape = shape,
//        content = content
//    )

}