package com.owenlejeune.tvtime.di.modules

import com.owenlejeune.tvtime.BuildConfig
import com.owenlejeune.tvtime.api.*
import com.owenlejeune.tvtime.preferences.AppPreferences
import com.owenlejeune.tvtime.utils.ResourceUtils
import org.koin.dsl.module

val networkModule = module {
    single { if (BuildConfig.DEBUG) DebugHttpClient() else ProdHttpClient() }
    single<Converter> { GsonConverter() }
    single { (baseUrl: String) -> Client(baseUrl) }
}

val preferencesModule = module {
    single { AppPreferences(get()) }
}

val appModule = module {
    factory { ResourceUtils(get()) }
}