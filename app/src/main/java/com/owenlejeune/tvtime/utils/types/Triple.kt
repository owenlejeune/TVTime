package com.owenlejeune.tvtime.utils.types

import java.io.Serializable

/**
 * Data class like Kotlin [Pair] but supporting three data items
 */
data class Triple<out A, out B, out C>(
    val first: A,
    val second: B,
    val third: C
) : Serializable {

    /**
     * Returns string representation of the [Triple] including its [first], [second], and [third] values.
     */
    override fun toString(): String = "($first, $second, $third)"
}