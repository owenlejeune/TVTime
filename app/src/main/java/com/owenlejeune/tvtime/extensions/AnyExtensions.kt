package com.owenlejeune.tvtime.extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun Any.coroutineTask(runnable: suspend () -> Unit) {
    CoroutineScope(Dispatchers.IO).launch { runnable() }
}

fun <T> anyOf(vararg items: T, predicate: (T) -> Boolean): Boolean = items.any(predicate)

fun <T: Any> T.isIn(vararg items: T): Boolean = items.any { it == this }

fun <T> pairOf(a: T, b: T) = Pair(a, b)

fun createEpisodeKey(seriesId: Int, seasonNumber: Int, episodeNumber: Int): String
    = listOf(seriesId, seasonNumber, episodeNumber).joinToString("_")