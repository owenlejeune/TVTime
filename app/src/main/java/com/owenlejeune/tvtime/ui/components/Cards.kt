package com.owenlejeune.tvtime.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.extensions.dpToPx

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
                    modifier = Modifier.padding(start = 12.dp, top = 8.dp),
                    color = textColor
                )
            }
            content()
        }
    }
}

@Composable
fun ImageTextCard(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    imageUrl: String? = null,
    noDataImage: Int = R.drawable.placeholder,
    placeholder: Int = R.drawable.placeholder,
    titleTextColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    subtitleTextColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .padding(end = 12.dp)
    ) {
        Image(
            modifier = Modifier
                .size(width = 120.dp, height = 180.dp),
            painter = rememberImagePainter(
                data = imageUrl ?: noDataImage,
                builder = {
                    transformations(RoundedCornersTransformation(5f.dpToPx(context)))
                    placeholder(placeholder)
                }
            ),
            contentDescription = ""
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