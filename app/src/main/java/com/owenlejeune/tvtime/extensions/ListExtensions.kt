package com.owenlejeune.tvtime.extensions

fun <T> List<T>.lastOr(provider: () -> T): T {
    return if (isNotEmpty()) {
        last()
    } else {
        provider()
    }
}

fun <T> List<T>.bringToFront(predicate: (T) -> Boolean): List<T> {
    val orig = toMutableList()
    val frontItems = emptyList<T>().toMutableList()
    forEach { i ->
        if (predicate(i)) {
            frontItems.add(i)
            orig.remove(i)
        }
    }
    return frontItems.plus(orig)
}

fun <T> List<T>.subListLimit(limit: Int, fromIndex: Int = 0): List<T> {
    return subList(fromIndex = fromIndex, toIndex = minOf(size, fromIndex+limit))
}