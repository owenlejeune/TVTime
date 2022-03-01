package com.owenlejeune.tvtime.api.tmdb

import androidx.compose.ui.text.intl.Locale
import com.owenlejeune.tvtime.BuildConfig
import com.owenlejeune.tvtime.api.Client
import com.owenlejeune.tvtime.api.QueryParam
import com.owenlejeune.tvtime.extensions.addQueryParams
import okhttp3.Interceptor
import okhttp3.Response
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class TmdbClient: KoinComponent {

    companion object {
        const val BASE_URL = "https://api.themoviedb.org/3/"
    }

    private val client: Client by inject { parametersOf(BASE_URL) }

    init {
        client.addInterceptor(TmdbInterceptor())
    }

    fun createMovieService(): MoviesApi {
        return client.create(MoviesApi::class.java)
    }

    fun createTvService(): TvApi {
        return client.create(TvApi::class.java)
    }

    fun createPeopleService(): PeopleApi {
        return client.create(PeopleApi::class.java)
    }

    fun createAuthenticationService(): AuthenticationApi {
        return client.create(AuthenticationApi::class.java)
    }

    fun createGuestSessionService(): GuestSessionApi {
        return client.create(GuestSessionApi::class.java)
    }

    private inner class TmdbInterceptor: Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val apiParam = QueryParam("api_key", BuildConfig.TMDB_ApiKey)

            val locale = Locale.current
            val languageCode = "${locale.language}-${locale.region}"
            val languageParam = QueryParam("language", languageCode)

            val request = chain.addQueryParams(apiParam, languageParam)

            return chain.proceed(request)
        }
    }

}