package com.owenlejeune.tvtime.extensions

fun <T> List<T>.lastOr(provider: () -> T): T {
    return if (isNotEmpty()) {
        last()
    } else {
        provider()
    }
}