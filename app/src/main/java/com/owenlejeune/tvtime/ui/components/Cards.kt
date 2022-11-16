package com.owenlejeune.tvtime.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.owenlejeune.tvtime.R

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
        backgroundColor = backgroundColor,
        elevation = 8.dp
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
            .fillMaxWidth()
            .wrapContentHeight()
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            ),
        shape = RoundedCornerShape(10.dp),
        backgroundColor = backgroundColor,
        elevation = 8.dp
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
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
        backgroundColor = backgroundColor,
        elevation = 8.dp
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
        backgroundColor = backgroundColor,
        elevation = 8.dp
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