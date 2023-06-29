package com.owenlejeune.tvtime.extensions

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.paging.compose.LazyPagingItems

fun <T: Any> LazyGridScope.lazyPagingItems(
    lazyPagingItems: LazyPagingItems<T>,
    key: ((index: Int) -> Any)? = null,
    itemContent: @Composable LazyGridItemScope.(value: T?) -> Unit
) {
    items(lazyPagingItems.itemCount, key = key) { index ->
        itemContent(lazyPagingItems[index])
    }
}

fun LazyGridScope.header(
    content: @Composable LazyGridItemScope.() -> Unit
) {
    item(
        span = {
            GridItemSpan(maxLineSpan)
        },
        content = content
    )
}

fun <T: Any> LazyListScope.listItems(
    items: List<T>,
    key: ((T) -> Any)? = null,
    itemContent: @Composable (value: T) -> Unit
) {
    items(items.size, key = key?.let { { key(items[it]) } }) { index ->
        itemContent(items[index])
    }
}

fun <T: Any> LazyListScope.lazyPagingItems(
    lazyPagingItems: LazyPagingItems<T>,
    key: ((index: Int) -> Any)? = null,
    itemContent: @Composable LazyItemScope.(value: T?) -> Unit
) {
    items(lazyPagingItems.itemCount, key = key) { index ->
        itemContent(lazyPagingItems[index])
    }
}

@Composable
fun Color.unlessDarkMode(other: Color): Color {
    return if (isSystemInDarkTheme()) this else other
}