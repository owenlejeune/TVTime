package com.owenlejeune.tvtime.extensions

import java.util.Calendar
import java.util.Date

fun Date.getCalendarYear(): Int {
    return Calendar.getInstance().apply {
        time = this@getCalendarYear
    }.get(Calendar.YEAR)
}