package com.owenlejeune.tvtime.utils

import androidx.compose.ui.graphics.Color

object ComposeUtils {

    fun colorToHexString(color: Color): String {
        val a = (color.alpha * 255f).toInt().toString(16).uppercase()
        val r = (color.red * 255f).toInt().toString(16).uppercase()
        val g = (color.green * 255f).toInt().toString(16).uppercase()
        val b = (color.blue * 255f).toInt().toString(16).uppercase()

        return "0x$a$r$g$b"
    }

}