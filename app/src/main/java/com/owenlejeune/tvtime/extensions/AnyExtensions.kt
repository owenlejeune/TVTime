package com.owenlejeune.tvtime.extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun Any.coroutineTask(runnable: suspend () -> Unit) {
    CoroutineScope(Dispatchers.IO).launch { runnable() }
}

fun <T> anyOf(vararg items: T, predicate: (T) -> Boolean): Boolean = items.any(predicate)