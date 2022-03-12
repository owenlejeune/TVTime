package com.owenlejeune.tvtime.extensions

import android.text.TextUtils
import android.util.Patterns

fun String.isEmailValid(): Boolean {
    return !TextUtils.isEmpty(this) && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.unlessEmpty(other: String): String {
    return this.ifEmpty { other }
}