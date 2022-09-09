package com.owenlejeune.tvtime.preferences

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.kieronquinn.monetcompat.core.MonetCompat
import com.owenlejeune.tvtime.BuildConfig
import com.owenlejeune.tvtime.utils.SessionManager

class AppPreferences(context: Context) {

    enum class DarkMode {
        Automatic,
        Dark,
        Light
    }

    companion object {
        private val PREF_FILE = "tvtime_shared_preferences"

        private val PERSISTENT_SEARCH = "persistent_search"
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
        private val DARK_THEME = "dark_theme"
        private val MULTI_SEARCH = "multi_search"
        private val MOVIES_TAB_POSITION = "movies_tab_position"
        private val TV_TAB_POSITION = "tv_tab_position"
        private val PEOPLE_TAB_POSITION = "people_tab_position"
        private val ACCOUNT_TAB_POSITION = "account_tab_position"
        private val SHOW_BTAB_LABELS = "show_btab_labels"
    }

    private val preferences: SharedPreferences = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)

    /******** Search Preferences ********/
    val showSearchBarDefault: Boolean = false
    var showSearchBar: Boolean
        get() = preferences.getBoolean(PERSISTENT_SEARCH, showSearchBarDefault)
        set(value) { preferences.put(PERSISTENT_SEARCH, value) }

    val multiSearchDefault: Boolean = true
    var multiSearch: Boolean
        get() = preferences.getBoolean(MULTI_SEARCH, multiSearchDefault)
        set(value) { preferences.put(MULTI_SEARCH, value) }

    /******* Design Preferences ********/
    val useWallpaperColorsDefault: Boolean = true
    var useWallpaperColors: Boolean
        get() = preferences.getBoolean(USE_WALLPAPER_COLORS, useWallpaperColorsDefault)
        set(value) { preferences.put(USE_WALLPAPER_COLORS, value) }

    val darkThemeDefault: Int = DarkMode.Automatic.ordinal
    var darkTheme: Int
        get() = preferences.getInt(DARK_THEME, darkThemeDefault)
        set(value) { preferences.put(DARK_THEME, value) }

    val useSystemColorsDefault: Boolean = true
    var useSystemColors: Boolean
        get() = preferences.getBoolean(USE_SYSTEM_COLORS, useSystemColorsDefault)
        set(value) { preferences.put(USE_SYSTEM_COLORS, value) }

    val chromeMultiplyerDefault: Double = MonetCompat.chromaMultiplier.toFloat().toDouble()
    var chromaMultiplier: Double
        get() = preferences.getFloat(CHROMA_MULTIPLIER, chromeMultiplyerDefault.toFloat()).toDouble()
        set(value) { preferences.put(CHROMA_MULTIPLIER, value) }

    val selectedColorDefault: Int = Int.MAX_VALUE
    var selectedColor: Int
        get() = preferences.getInt(SELECTED_COLOR, selectedColorDefault)
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

    /******** Home Screen Preferences ********/
    val moviesTabPositionDefault: Int = 0
    var moviesTabPosition: Int
        get() = preferences.getInt(MOVIES_TAB_POSITION, moviesTabPositionDefault)
        set(value) { preferences.put(MOVIES_TAB_POSITION, value) }

    val tvTabPositionDefault: Int = 1
    var tvTabPosition: Int
        get() = preferences.getInt(TV_TAB_POSITION, tvTabPositionDefault)
        set(value) { preferences.put(TV_TAB_POSITION, value) }

    val peopleTabPositionDefault: Int = 2
    var peopleTabPosition: Int
        get() = preferences.getInt(PEOPLE_TAB_POSITION, peopleTabPositionDefault)
        set(value) { preferences.put(PEOPLE_TAB_POSITION, value) }

    val accountTabPositionDefault: Int = 3
    var accountTabPosition: Int
        get() = preferences.getInt(ACCOUNT_TAB_POSITION, accountTabPositionDefault)
        set(value) { preferences.put(ACCOUNT_TAB_POSITION, value) }

    val showBottomTabLabelsDefault: Boolean = true
    var showBottomTabLabels: Boolean
        get() = preferences.getBoolean(SHOW_BTAB_LABELS, showBottomTabLabelsDefault)
        set(value) { preferences.put(SHOW_BTAB_LABELS, value) }

    /******** Dev Preferences ********/
    val firstLaunchTestingDefault: Boolean = false
    var firstLaunchTesting: Boolean
        get() = preferences.getBoolean(FIRST_LAUNCH_TESTING, firstLaunchTestingDefault)
        set(value) { preferences.put(FIRST_LAUNCH_TESTING, value) }

    var firstLaunch: Boolean
        get() = if (BuildConfig.DEBUG) firstLaunchTesting else preferences.getBoolean(FIRST_LAUNCH, true)
        set(value) { preferences.put(FIRST_LAUNCH, value) }

    val useV4ApiDefault: Boolean = false
    var useV4Api: Boolean
        get() = preferences.getBoolean(USE_V4_API, useV4ApiDefault)
        set(value) { preferences.put(USE_V4_API, value) }

    val showBackdropGalleryDefault: Boolean = true
    var showBackdropGallery: Boolean
        get() = preferences.getBoolean(SHOW_BACKDROP_GALLERY, showBackdropGalleryDefault)
        set(value) { preferences.put(SHOW_BACKDROP_GALLERY, value) }

    /********* Helpers ********/
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