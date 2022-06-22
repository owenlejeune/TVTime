package com.owenlejeune.tvtime.preferences

import android.content.Context
import android.content.SharedPreferences
import com.kieronquinn.monetcompat.core.MonetCompat
import com.owenlejeune.tvtime.BuildConfig

class AppPreferences(context: Context) {

    companion object {
        private val PREF_FILE = "tvtime_shared_preferences"

//        private val USE_PREFERENCES = "use_android_12_colors"
        private val PERSISTENT_SEARCH = "persistent_search"
        private val HIDE_TITLE = "hide_title"
        private val GUEST_SESSION = "guest_session_id"
        private val AUTHORIZED_SESSION = "authorized_session_id"
        private val FIRST_LAUNCH = "first_launch"
        private val FIRST_LAUNCH_TESTING = "first_launch_testing"
        private val CHROMA_MULTIPLIER = "chroma_multiplier"
        private val USE_SYSTEM_COLORS = "use_system_colors"
        private val SELECTED_COLOR = "selected_color"
    }

    private val preferences: SharedPreferences = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)

    var persistentSearch: Boolean
        get() = preferences.getBoolean(PERSISTENT_SEARCH, true)
        set(value) { preferences.put(PERSISTENT_SEARCH, value) }

    var hideTitle: Boolean
        get() = preferences.getBoolean(HIDE_TITLE, false)
        set(value) { preferences.put(HIDE_TITLE, value) }

    var guestSessionId: String
        get() = preferences.getString(GUEST_SESSION, "") ?: ""
        set(value) { preferences.put(GUEST_SESSION, value) }

    var authorizedSessionId: String
        get() = preferences.getString(AUTHORIZED_SESSION, "") ?: ""
        set(value) { preferences.put(AUTHORIZED_SESSION, value) }
//    val usePreferences:  MutableState<Boolean>
//    var usePreferences: Boolean
//        get() = preferences.getBoolean(USE_PREFERENCES, false)
//        set(value) { preferences.put(USE_PREFERENCES, value) }

    var firstLaunchTesting: Boolean
        get() = preferences.getBoolean(FIRST_LAUNCH_TESTING, false)
        set(value) { preferences.put(FIRST_LAUNCH_TESTING, value) }

    var firstLaunch: Boolean
        get() = if (BuildConfig.DEBUG) firstLaunchTesting else preferences.getBoolean(FIRST_LAUNCH, true)
        set(value) { preferences.put(FIRST_LAUNCH, value) }

    var useSystemColors: Boolean
        get() = preferences.getBoolean(USE_SYSTEM_COLORS, true)
        set(value) { preferences.put(USE_SYSTEM_COLORS, value) }

    var chromaMultiplier: Double
        get() = preferences.getFloat(CHROMA_MULTIPLIER, MonetCompat.chromaMultiplier.toFloat()).toDouble()
        set(value) { preferences.put(CHROMA_MULTIPLIER, value) }

    var selectedColor: Int
        get() = preferences.getInt(SELECTED_COLOR, Int.MAX_VALUE)
        set(value) { preferences.put(SELECTED_COLOR, value) }

    private fun SharedPreferences.put(key: String, value: Any?) {
        edit().apply {
            when (value) {
                is Boolean -> putBoolean(key, value)
                is Int -> putInt(key, value)
                is Long -> putLong(key, value)
                is Float -> putFloat(key, value)
                is Double -> putFloat(key, value.toFloat())
                is String -> putString(key, value)
                else -> throw UnsupportedTypeError()
            }
            apply()
        }
    }

    class UnsupportedTypeError: Exception()

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