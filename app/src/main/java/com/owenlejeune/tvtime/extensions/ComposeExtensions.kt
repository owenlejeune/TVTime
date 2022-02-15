package com.owenlejeune.tvtime.extensions

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.lazy.LazyGridScope
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.paging.compose.LazyPagingItems

@OptIn(ExperimentalFoundationApi::class)
fun <T: Any> LazyGridScope.lazyPagingItems(
    lazyPagingItems: LazyPagingItems<T>,
    itemContent: @Composable LazyItemScope.(value: T?) -> Unit
) {
    items(lazyPagingItems.itemCount) { index ->
        itemContent(lazyPagingItems[index])
    }
}

@OptIn(ExperimentalFoundationApi::class)
fun <T: Any> LazyGridScope.listItems(
    items: List<T>,
    itemContent: @Composable (value: T) -> Unit
) {
    items(items.size) { index ->
        itemContent(items[index])
    }
}

fun <T: Any> LazyListScope.listItems(
    items: List<T>,
    itemContent: @Composable (value: T) -> Unit
) {
    items(items.size) { index ->
        itemContent(items[index])
    }
}

@Composable
fun Color.unlessDarkMode(other: Color): Color {
    return if (isSystemInDarkTheme()) this else other
}