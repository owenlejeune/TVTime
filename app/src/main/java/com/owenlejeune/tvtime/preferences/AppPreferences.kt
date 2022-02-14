package com.owenlejeune.tvtime.preferences

import android.content.Context
import android.content.SharedPreferences

class AppPreferences(context: Context) {

    companion object {
        private val PREF_FILE = "tvtime_shared_preferences"

        private val USE_PREFERENCES = "use_android_12_colors"
    }

    private val preferences: SharedPreferences = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)

//    val usePreferences:  MutableState<Boolean>
    var usePreferences: Boolean
        get() = preferences.getBoolean(USE_PREFERENCES, false)
        set(value) { preferences.put(USE_PREFERENCES, value) }

    private fun SharedPreferences.put(key: String, value: Any?) {
        edit().apply {
            when (value) {
                is Boolean -> putBoolean(key, value)
                is Int -> putInt(key, value)
                is Long -> putLong(key, value)
                is Float -> putFloat(key, value)
                is String -> putString(key, value)
            }
            apply()
        }
    }

//    private fun <T> SharedPreferences.get(key: String, java: Class<T>): T {
//
//    }
//
//    inner class PrefsMutableState<T>(val key: String): MutableState<T> {
//        override var value: T
//            get() = preferences.get(key, T::class.java)
//            set(value) { preferences.put(key, value) }
//
//        override fun component1(): T {
//            TODO("Not yet implemented")
//        }
//
//        override fun component2(): (T) -> Unit {
//            TODO("Not yet implemented")
//        }
//
//    }

}