package com.owenlejeune.tvtime.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
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

@Composable
fun ExpandableContentCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    title: @Composable () -> Unit = {},
    collapsedText: String = stringResource(id = R.string.expandable_see_more),
    expandedText: String = stringResource(id = R.string.expandable_see_less),
    toggleTextColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    expandOnTouchAnywhere: Boolean = false,
    content: @Composable (Boolean) -> Unit
) {
    var expandedState by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.then(
            Modifier.clickable(
                enabled = expandOnTouchAnywhere,
                onClick = {
                    expandedState = true
                }
            )
        ),
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
    hideSubtitle: Boolean = false,
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
        if (!hideSubtitle) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 2,
                text = subtitle ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = subtitleTextColor,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}