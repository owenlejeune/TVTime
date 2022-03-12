package com.owenlejeune.tvtime.api.tmdb

import androidx.compose.ui.text.intl.Locale
import com.owenlejeune.tvtime.BuildConfig
import com.owenlejeune.tvtime.api.Client
import com.owenlejeune.tvtime.api.QueryParam
import com.owenlejeune.tvtime.extensions.addQueryParams
import com.owenlejeune.tvtime.preferences.AppPreferences
import com.owenlejeune.tvtime.utils.SessionManager
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
    private val preferences: AppPreferences by inject()

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

    fun createAccountService(): AccountApi {
        return client.create(AccountApi::class.java)
    }

    private inner class TmdbInterceptor: Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val apiParam = QueryParam("api_key", BuildConfig.TMDB_ApiKey)

            val locale = Locale.current
            val languageCode = "${locale.language}-${locale.region}"
            val languageParam = QueryParam("language", languageCode)

            val segments = chain.request().url.encodedPathSegments
            val sessionIdParam: QueryParam? = sessionIdParam(segments)

            val request = chain.addQueryParams(apiParam, languageParam, sessionIdParam)

            return chain.proceed(request)
        }
    }

    private fun sessionIdParam(urlSegments: List<String>): QueryParam? {
        var sessionIdParam: QueryParam? = null
        if (urlSegments.size > 1 && urlSegments[1] == "account") {
            if (SessionManager.currentSession?.isAuthorized == true) {
                sessionIdParam = QueryParam("session_id", SessionManager.currentSession!!.sessionId)
            } else if (preferences.authorizedSessionId.isNotEmpty()) {
                sessionIdParam = QueryParam("session_id", preferences.authorizedSessionId)
            }
        }
        return sessionIdParam
    }

}