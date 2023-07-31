package com.owenlejeune.tvtime.di.modules

import com.google.gson.GsonBuilder
import com.owenlejeune.tvtime.BuildConfig
import com.owenlejeune.tvtime.api.*
import com.owenlejeune.tvtime.api.nextmcu.NextMCUClient
import com.owenlejeune.tvtime.api.nextmcu.NextMCUService
import com.owenlejeune.tvtime.api.tmdb.TmdbClient
import com.owenlejeune.tvtime.api.tmdb.api.v3.AccountService
import com.owenlejeune.tvtime.api.tmdb.api.v3.AuthenticationService
import com.owenlejeune.tvtime.api.tmdb.api.v3.ConfigurationService
import com.owenlejeune.tvtime.api.tmdb.api.v3.CreditsService
import com.owenlejeune.tvtime.api.tmdb.api.v3.MoviesService
import com.owenlejeune.tvtime.api.tmdb.api.v3.PeopleService
import com.owenlejeune.tvtime.api.tmdb.api.v3.SearchService
import com.owenlejeune.tvtime.api.tmdb.api.v3.TvService
import com.owenlejeune.tvtime.api.tmdb.api.v3.deserializer.AccountStatesDeserializer
import com.owenlejeune.tvtime.api.tmdb.api.v3.deserializer.CreditMediaDeserializer
import com.owenlejeune.tvtime.api.tmdb.api.v3.deserializer.DetailCastDeserializer
import com.owenlejeune.tvtime.api.tmdb.api.v3.deserializer.DetailCrewDeserializer
import com.owenlejeune.tvtime.api.tmdb.api.v3.deserializer.KnownForDeserializer
import com.owenlejeune.tvtime.api.tmdb.api.v3.deserializer.SeasonAccountStatesResultDeserializer
import com.owenlejeune.tvtime.api.tmdb.api.v3.deserializer.SortableSearchResultDeserializer
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.AccountStates
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.CreditMedia
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.DetailCast
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.DetailCrew
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.KnownFor
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.EpisodeAccountState
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.SortableSearchResult
import com.owenlejeune.tvtime.api.tmdb.api.v4.AccountV4Service
import com.owenlejeune.tvtime.api.tmdb.api.v4.AuthenticationV4Service
import com.owenlejeune.tvtime.api.tmdb.api.v4.ListV4Service
import com.owenlejeune.tvtime.api.tmdb.api.v4.deserializer.ListItemDeserializer
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.ListItem
import com.owenlejeune.tvtime.preferences.AppPreferences
import com.owenlejeune.tvtime.ui.viewmodel.ConfigurationViewModel
import com.owenlejeune.tvtime.utils.NetworkConnectivityService
import com.owenlejeune.tvtime.utils.NetworkConnectivityServiceImpl
import com.owenlejeune.tvtime.utils.ResourceUtils
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.util.Date

val networkModule = module {
    single { if (BuildConfig.DEBUG) DebugHttpClient() else ProdHttpClient() }
    single<ConverterFactoryFactory> { GsonConverter() }
    factory { (baseUrl: String) -> Client(baseUrl) }

    single { TmdbClient() }
    single { get<TmdbClient>().createV4AuthenticationService() }
    single { get<TmdbClient>().createV4AccountService() }
    single { get<TmdbClient>().createV4ListService() }
    single { get<TmdbClient>().createAccountService() }
    single { get<TmdbClient>().createAuthenticationService() }
    single { get<TmdbClient>().createMovieService() }
    single { get<TmdbClient>().createPeopleService() }
    single { get<TmdbClient>().createSearchService() }
    single { get<TmdbClient>().createTvService() }
    single { get<TmdbClient>().createConfigurationService() }
    single { get<TmdbClient>().createCreditsService() }

    single { ConfigurationService() }
    single { MoviesService() }
    single { TvService() }
    single { AccountService() }
    single { AuthenticationService() }
    single { PeopleService() }
    single { SearchService() }
    single { CreditsService() }
    single { AccountV4Service() }
    single { AuthenticationV4Service() }
    single { ListV4Service() }

    single { NextMCUClient() }
    single { get<NextMCUClient>().createNextMcuService() }
    single { NextMCUService() }

    single<Map<Class<*>, Any>> {
        mapOf(
            ListItem::class.java to ListItemDeserializer(),
            KnownFor::class.java to KnownForDeserializer(),
            SortableSearchResult::class.java to SortableSearchResultDeserializer(),
            AccountStates::class.java to AccountStatesDeserializer(),
            DetailCast::class.java to DetailCastDeserializer(),
            DetailCrew::class.java to DetailCrewDeserializer(),
            CreditMedia::class.java to CreditMediaDeserializer(),
            Date::class.java to DateTypeAdapter(),
            EpisodeAccountState::class.java to SeasonAccountStatesResultDeserializer()
        )
    }

    single {
        GsonBuilder().apply {
            get<Map<Class<*>, Any>>().forEach { des ->
                registerTypeAdapter(des.key, des.value)
            }
        }.create()
    }
}

val preferencesModule = module {
    single { AppPreferences(get()) }
}

val appModule = module {
    factory { ResourceUtils(get()) }

    single<NetworkConnectivityService> { NetworkConnectivityServiceImpl() }
}

val viewModelModule = module {
    viewModel { ConfigurationViewModel() }
}