package com.owenlejeune.tvtime.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.owenlejeune.tvtime.extensions.listItems

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
private fun PaletteItemView(surfaceColor: Color, textColor: Color, text: String) {
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