package com.owenlejeune.tvtime.preferences

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.kieronquinn.monetcompat.core.MonetCompat
import com.owenlejeune.tvtime.BuildConfig
import com.owenlejeune.tvtime.utils.SessionManager
import java.util.function.BiPredicate

class AppPreferences(context: Context) {

    companion object {
        private val PREF_FILE = "tvtime_shared_preferences"

//        private val USE_PREFERENCES = "use_android_12_colors"
        private val PERSISTENT_SEARCH = "persistent_search"
        private val HIDE_TITLE = "hide_title"
        private val GUEST_SESSION = "guest_session_id"
        private val AUTHORIZED_SESSION = "authorized_session_id"
        private val AUTHORIZED_SESSION_VALUES = "authorized_session_values"
        private val FIRST_LAUNCH = "first_launch"
        private val FIRST_LAUNCH_TESTING = "first_launch_testing"
        private val CHROMA_MULTIPLIER = "chroma_multiplier"
        private val USE_SYSTEM_COLORS = "use_system_colors"
        private val SELECTED_COLOR = "selected_color"
        private val USE_V4_API = "use_v4_api"
        private val SHOW_BACKDROP_GALLERY = "show_backdrop_gallery"
        private val USE_WALLPAPER_COLORS = "use_wallpaper_colors"
    }

    private val preferences: SharedPreferences = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)

    /******** Search Preferences ********/
    var persistentSearch: Boolean
        get() = preferences.getBoolean(PERSISTENT_SEARCH, true)
        set(value) { preferences.put(PERSISTENT_SEARCH, value) }

    var hideTitle: Boolean
        get() = preferences.getBoolean(HIDE_TITLE, false)
        set(value) { preferences.put(HIDE_TITLE, value) }

    var useWallpaperColors: Boolean
        get() = preferences.getBoolean(USE_WALLPAPER_COLORS, true)
        set(value) { preferences.put(USE_WALLPAPER_COLORS, value) }


    /******* Design Preferences ********/
    var useSystemColors: Boolean
        get() = preferences.getBoolean(USE_SYSTEM_COLORS, true)
        set(value) { preferences.put(USE_SYSTEM_COLORS, value) }

    var chromaMultiplier: Double
        get() = preferences.getFloat(CHROMA_MULTIPLIER, MonetCompat.chromaMultiplier.toFloat()).toDouble()
        set(value) { preferences.put(CHROMA_MULTIPLIER, value) }

    var selectedColor: Int
        get() = preferences.getInt(SELECTED_COLOR, Int.MAX_VALUE)
        set(value) { preferences.put(SELECTED_COLOR, value) }

    /******* Session Tokens ********/
    var guestSessionId: String
        get() = preferences.getString(GUEST_SESSION, "") ?: ""
        set(value) { preferences.put(GUEST_SESSION, value) }

    var authorizedSessionValues: SessionManager.AuthorizedSessionValues?
        get() = preferences.getString(AUTHORIZED_SESSION_VALUES, null)?.let {
            Gson().fromJson(it, SessionManager.AuthorizedSessionValues::class.java)
        }
        set(value) { preferences.putNullableString(AUTHORIZED_SESSION_VALUES, value?.let { Gson().toJson(value) }) }

    var authorizedSessionId: String
        get() = preferences.getString(AUTHORIZED_SESSION, "") ?: ""
        set(value) { preferences.put(AUTHORIZED_SESSION, value) }

    /******** Dev Preferences ********/
    var firstLaunchTesting: Boolean
        get() = preferences.getBoolean(FIRST_LAUNCH_TESTING, false)
        set(value) { preferences.put(FIRST_LAUNCH_TESTING, value) }

    var firstLaunch: Boolean
        get() = if (BuildConfig.DEBUG) firstLaunchTesting else preferences.getBoolean(FIRST_LAUNCH, true)
        set(value) { preferences.put(FIRST_LAUNCH, value) }

    var useV4Api: Boolean
        get() = preferences.getBoolean(USE_V4_API, true)
        set(value) { preferences.put(USE_V4_API, value) }

    var showBackdropGallery: Boolean = true

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

    private fun SharedPreferences.putNullableString(key: String, value: String?) {
        edit().putString(key, value).apply()
    }

    class UnsupportedTypeError: Exception()

}