package com.owenlejeune.tvtime.extensions

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
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

fun Date.toLocalDate() = Instant.ofEpochMilli(time)
    .atZone(ZoneId.systemDefault())
    .toLocalDate()

fun Date.yearsSince(anchorDate: Date? = null): Int {
    val endDate = anchorDate?.toLocalDate() ?: LocalDate.now()
    val thisLocalDate = toLocalDate()

    return Period.between(thisLocalDate, endDate).years
}

enum class DateFormat(val format: String) {
    MMMM_dd("MMMM dd"),
    MMMM_dd_yyyy("MMMM dd yyyy")
}