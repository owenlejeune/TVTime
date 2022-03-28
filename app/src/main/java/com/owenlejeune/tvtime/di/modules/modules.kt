package com.owenlejeune.tvtime.di.modules

import com.google.gson.JsonDeserializer
import com.owenlejeune.tvtime.BuildConfig
import com.owenlejeune.tvtime.api.*
import com.owenlejeune.tvtime.api.tmdb.TmdbClient
import com.owenlejeune.tvtime.api.tmdb.api.v3.deserializer.KnownForDeserializer
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.KnownFor
import com.owenlejeune.tvtime.api.tmdb.api.v4.deserializer.ListItemDeserializer
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.ListItem
import com.owenlejeune.tvtime.preferences.AppPreferences
import com.owenlejeune.tvtime.utils.ResourceUtils
import org.koin.dsl.module

val networkModule = module {
    single { if (BuildConfig.DEBUG) DebugHttpClient() else ProdHttpClient() }
    single<ConverterFactoryFactory> { GsonConverter() }
    factory { (baseUrl: String) -> Client(baseUrl) }

    single { TmdbClient() }
    single { get<TmdbClient>().createV4AuthenticationService() }
    single { get<TmdbClient>().createAccountService() }
    single { get<TmdbClient>().createGuestSessionService() }
    single { get<TmdbClient>().createAuthenticationService() }
    single { get<TmdbClient>().createMovieService() }
    single { get<TmdbClient>().createPeopleService() }
    single { get<TmdbClient>().createSearchService() }
    single { get<TmdbClient>().createTvService() }

    single<Map<Class<*>, JsonDeserializer<*>>> {
        mapOf(
            ListItem::class.java to ListItemDeserializer(),
            KnownFor::class.java to KnownForDeserializer()
        )
    }
}

val preferencesModule = module {
    single { AppPreferences(get()) }
}

val appModule = module {
    factory { ResourceUtils(get()) }
}