package com.owenlejeune.tvtime.api.tmdb

import androidx.compose.ui.text.intl.Locale
import com.owenlejeune.tvtime.BuildConfig
import com.owenlejeune.tvtime.api.Client
import com.owenlejeune.tvtime.api.QueryParam
import com.owenlejeune.tvtime.api.tmdb.api.v3.*
import com.owenlejeune.tvtime.api.tmdb.api.v4.AccountV4Api
import com.owenlejeune.tvtime.api.tmdb.api.v4.AuthenticationV4Api
import com.owenlejeune.tvtime.api.tmdb.api.v4.ListV4Api
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
        const val V_3_BASE_URL = "https://api.themoviedb.org/3/"
        const val V_4_BASE_URL = "https://api.themoviedb.org/4/"
    }

    private val client: Client by inject { parametersOf(V_3_BASE_URL) }
    private val clientV4: Client by inject { parametersOf(V_4_BASE_URL) }
    private val preferences: AppPreferences by inject()

    init {
        client.addInterceptor(TmdbInterceptor())
        clientV4.addInterceptor(V4Interceptor())
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

    fun createV4AuthenticationService(): AuthenticationV4Api {
        return clientV4.create(AuthenticationV4Api::class.java)
    }

    fun createGuestSessionService(): GuestSessionApi {
        return client.create(GuestSessionApi::class.java)
    }

    fun createAccountService(): AccountApi {
        return client.create(AccountApi::class.java)
    }

    fun createV4AccountService(): AccountV4Api {
        return clientV4.create(AccountV4Api::class.java)
    }

    fun createSearchService(): SearchApi {
        return client.create(SearchApi::class.java)
    }

    fun createV4ListService(): ListV4Api {
        return clientV4.create(ListV4Api::class.java)
    }

    private inner class TmdbInterceptor: Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val apiParam = QueryParam("api_key", BuildConfig.TMDB_ApiKey)

            val segments = chain.request().url.encodedPathSegments
            val sessionIdParam: QueryParam? = sessionIdParam(segments)

            val builder = chain.request().url.newBuilder()
            builder.addQueryParams(apiParam, sessionIdParam)

            if (shouldIncludeLanguageParam(segments)) {
                val locale = Locale.current
                val languageCode = "${locale.language}-${locale.region}"
                val languageParam = QueryParam("language", languageCode)

                builder.addQueryParams(languageParam)
            }

            if (shouldIncludeRegionParam(segments)) {
                val locale = Locale.current
                val regionParam = QueryParam("region", locale.region)

                builder.addQueryParams(regionParam)
            }

            val requestBuilder = chain.request().newBuilder().url(builder.build())

            val request = requestBuilder.build()
            return chain.proceed(request)
        }

        private fun sessionIdParam(urlSegments: List<String>): QueryParam? {
            var sessionIdParam: QueryParam? = null
            if (urlSegments.size > 1 && urlSegments[1] == "account") {
                if (SessionManager.currentSession?.isAuthorized == true) {
                    sessionIdParam = QueryParam("session_id", SessionManager.currentSession!!.sessionId)
                } else if (preferences.authorizedSessionId.isNotEmpty()) {
                    sessionIdParam = QueryParam("session_id", preferences.authorizedSessionId)
                } else if (preferences.authorizedSessionValues != null) {
                    val sessionId = preferences.authorizedSessionValues!!.sessionId
                    sessionIdParam = QueryParam("session_id", sessionId)
                }
            }
            return sessionIdParam
        }

        private fun shouldIncludeLanguageParam(urlSegments: List<String>): Boolean {
            val ignoredRoutes = listOf("images")
            for (route in ignoredRoutes) {
                if (urlSegments.contains(route)) {
                    return false
                }
            }
            return true
        }

        private fun shouldIncludeRegionParam(urlSegments: List<String>): Boolean {
            val includedRoutes = listOf("search")
            for (route in includedRoutes) {
                if (urlSegments.contains(route)) {
                    return true
                }
            }
            return false
        }
    }

    private inner class V4Interceptor: Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val builder = chain.request().newBuilder()
            with(chain.request()) {
                if (url.encodedPathSegments.contains("auth")) {
                    builder.header("Authorization", "Bearer ${BuildConfig.TMDB_Api_v4Key}")
                } else {
                    builder.header("Authorization", "Bearer ${SessionManager.currentSession!!.accessToken}")
                    val locale = Locale.current
                    val languageCode = "${locale.language}-${locale.region}"
                    val languageParam = QueryParam("language", languageCode)

                    val newUrl = url.newBuilder().addQueryParams(languageParam).build()
                    builder.url(newUrl)
                }
            }

            return chain.proceed(builder.build())
        }
    }

}