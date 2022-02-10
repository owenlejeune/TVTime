package com.owenlejeune.tvtime.extensions

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyGridScope
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.runtime.Composable
import androidx.paging.compose.LazyPagingItems

@OptIn(ExperimentalFoundationApi::class)
fun <T: Any> LazyGridScope.items(
    lazyPagingItems: LazyPagingItems<T>,
    itemContent: @Composable LazyItemScope.(value: T?) -> Unit
) {
    items(lazyPagingItems.itemCount) { index ->
        itemContent(lazyPagingItems[index])
    }
}

@OptIn(ExperimentalFoundationApi::class)
fun <T: Any> LazyGridScope.items(
    items: List<T>,
    itemContent: @Composable (value: T?) -> Unit
) {
    items(items.size) { index ->
        itemContent(items[index])
    }
}