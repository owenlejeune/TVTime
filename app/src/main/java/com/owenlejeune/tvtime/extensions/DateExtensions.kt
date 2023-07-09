package com.owenlejeune.tvtime.extensions

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun Date.getCalendarYear(): Int {
    return Calendar.getInstance().apply {
        time = this@getCalendarYear
    }.get(Calendar.YEAR)
}

fun Date.format(format: DateFormat): String {
    val formatter = SimpleDateFormat(format.format, Locale.getDefault())
    return formatter.format(this)
}

enum class DateFormat(val format: String) {
    MMMM_dd("MMMM dd")
}