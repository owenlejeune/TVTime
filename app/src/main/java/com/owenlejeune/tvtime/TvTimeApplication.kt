package com.owenlejeune.tvtime

import android.app.Application
import com.facebook.stetho.Stetho
import com.kieronquinn.monetcompat.core.MonetCompat
import com.owenlejeune.tvtime.di.modules.appModule
import com.owenlejeune.tvtime.di.modules.networkModule
import com.owenlejeune.tvtime.di.modules.preferencesModule
import com.owenlejeune.tvtime.di.modules.viewModelModule
import com.owenlejeune.tvtime.preferences.AppPreferences
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class TvTimeApplication: Application() {

    private val preferences: AppPreferences by inject()

    override fun onCreate() {
        super.onCreate()

        // start koin
        startKoin {
            androidLogger(
                if (BuildConfig.DEBUG) Level.ERROR else Level.NONE
            )
            androidContext(this@TvTimeApplication)
            modules(
                networkModule,
                preferencesModule,
                appModule,
                viewModelModule
            )
        }

        MonetCompat.chromaMultiplier = preferences.chromaMultiplier

        MonetCompat.wallpaperColorPicker = {
            val userPickedColor = preferences.selectedColor
            it?.firstOrNull { color -> color == userPickedColor } ?: it?.firstOrNull()
        }

        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
        }
    }

}