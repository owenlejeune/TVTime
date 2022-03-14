package com.owenlejeune.tvtime.di.modules

import com.google.gson.JsonDeserializer
import com.owenlejeune.tvtime.BuildConfig
import com.owenlejeune.tvtime.api.*
import com.owenlejeune.tvtime.api.tmdb.api.v4.deserializer.ListItemDeserializer
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.ListItem
import com.owenlejeune.tvtime.preferences.AppPreferences
import com.owenlejeune.tvtime.utils.ResourceUtils
import org.koin.dsl.module
import java.lang.reflect.Type
import kotlin.reflect.KClass

val networkModule = module {
    single { if (BuildConfig.DEBUG) DebugHttpClient() else ProdHttpClient() }
    single<Converter> { GsonConverter() }
    single { (baseUrl: String) -> Client(baseUrl) }

    single<Map<Class<*>, JsonDeserializer<*>>> { mapOf(ListItem::class.java to ListItemDeserializer()) }
}

val preferencesModule = module {
    single { AppPreferences(get()) }
}

val appModule = module {
    factory { ResourceUtils(get()) }
}