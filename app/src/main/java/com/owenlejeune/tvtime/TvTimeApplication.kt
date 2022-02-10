package com.owenlejeune.tvtime

import android.app.Application
import com.facebook.stetho.Stetho
import com.owenlejeune.tvtime.di.modules.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class TvTimeApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        // start koin
        startKoin {
            androidLogger(
                if (BuildConfig.DEBUG) Level.ERROR else Level.NONE
            )
            androidContext(this@TvTimeApplication)
            modules(networkModule)
        }

        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
        }
    }

}