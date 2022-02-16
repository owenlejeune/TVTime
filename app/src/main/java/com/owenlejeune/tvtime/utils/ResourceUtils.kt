package com.owenlejeune.tvtime.utils

import android.content.Context

class ResourceUtils(private val context: Context) {

    fun getString(stringRes: Int): String {
        return context.getString(stringRes)
    }

}