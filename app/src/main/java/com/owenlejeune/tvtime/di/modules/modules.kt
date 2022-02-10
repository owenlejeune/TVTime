package com.owenlejeune.tvtime.di.modules

import com.owenlejeune.tvtime.BuildConfig
import com.owenlejeune.tvtime.api.*
import org.koin.dsl.module

val networkModule = module {
    single { if (BuildConfig.DEBUG) DebugHttpClient() else ProdHttpClient() }
    single<Converter> { GsonConverter() }
    single { (baseUrl: String) -> Client(baseUrl) }
}