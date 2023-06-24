package com.owenlejeune.tvtime.extensions

import android.os.Build
import android.os.Bundle
import java.io.Serializable
import kotlin.reflect.KClass

fun <T: Serializable> Bundle.safeGetSerializable(key: String, clazz: Class<T>): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getSerializable(key, clazz)
    } else {
        getSerializable(key) as? T
    }
}