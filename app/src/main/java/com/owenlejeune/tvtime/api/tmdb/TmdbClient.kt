package com.owenlejeune.tvtime.api.tmdb

import android.annotation.SuppressLint
import androidx.compose.ui.text.intl.Locale
import androidx.lifecycle.viewmodel.viewModelFactory
import com.owenlejeune.tvtime.BuildConfig
import com.owenlejeune.tvtime.api.Client
import com.owenlejeune.tvtime.api.QueryParam
import com.owenlejeune.tvtime.api.tmdb.api.v3.AccountApi
import com.owenlejeune.tvtime.api.tmdb.api.v3.AuthenticationApi
import com.owenlejeune.tvtime.api.tmdb.api.v3.ConfigurationApi
import com.owenlejeune.tvtime.api.tmdb.api.v3.CreditsApi
import com.owenlejeune.tvtime.api.tmdb.api.v3.MoviesApi
import com.owenlejeune.tvtime.api.tmdb.api.v3.PeopleApi
import com.owenlejeune.tvtime.api.tmdb.api.v3.SearchApi
import com.owenlejeune.tvtime.api.tmdb.api.v3.TvApi
import com.owenlejeune.tvtime.api.tmdb.api.v4.AccountV4Api
import com.owenlejeune.tvtime.api.tmdb.api.v4.AuthenticationV4Api
import com.owenlejeune.tvtime.api.tmdb.api.v4.ListV4Api
import com.owenlejeune.tvtime.extensions.addQueryParams
import com.owenlejeune.tvtime.preferences.AppPreferences
import com.owenlejeune.tvtime.ui.viewmodel.ConfigurationViewModel
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

    fun createAccountService(): AccountApi {
        return client.create(AccountApi::class.java)
    }

    fun createConfigurationService(): ConfigurationApi {
        return client.create(ConfigurationApi::class.java)
    }

    fun createCreditsService(): CreditsApi {
        return client.create(CreditsApi::class.java)
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

    @SuppressLint("AutoboxingStateValueProperty")
    private fun handleResponseCode(response: Response) {
        when (response.code) {
            429 -> {
                ConfigurationViewModel.lastResponseCode.value = 429
            }
            in 400..499 -> {
                ConfigurationViewModel.lastResponseCode.value = response.code
            }
            in 500..599 -> {
                ConfigurationViewModel.lastResponseCode.value = response.code
            }
        }
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
                val regionParam = QueryParam("region", locale.region)

                builder.addQueryParams(languageParam, regionParam)
            }

            if (shouldIncludeSessionIdParam(segments)) {
                val sessionIdParam = QueryParam("session_id", SessionManager.currentSession.value!!.sessionId)
                builder.addQueryParams(sessionIdParam)
            }

            val requestBuilder = chain.request().newBuilder().url(builder.build())

            val request = requestBuilder.build()
            val response = chain.proceed(request)

            handleResponseCode(response)

            return response
        }

        private fun sessionIdParam(urlSegments: List<String>): QueryParam? {
            var sessionIdParam: QueryParam? = null
            if (urlSegments.size > 1 && urlSegments[1] == "account") {
                val currentSession = SessionManager.currentSession.value
                if (!currentSession?.sessionId.isNullOrEmpty()) {
                    sessionIdParam = QueryParam("session_id", currentSession!!.sessionId)
                }
            }
            return sessionIdParam
        }

        private fun shouldIncludeLanguageParam(urlSegments: List<String>): Boolean {
            val ignoredRoutes = listOf("images", "account")
            for (route in ignoredRoutes) {
                if (urlSegments.contains(route)) {
                    return false
                }
            }
            return true
        }

        private fun shouldIncludeSessionIdParam(urlSegments: List<String>): Boolean {
            val includedRoutes = listOf("account_states")
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
                    builder.header("Authorization", "Bearer ${SessionManager.currentSession.value!!.accessToken}")
                }
                if (shouldIncludeLanguageParam(url.encodedPathSegments)) {
                    val locale = Locale.current
                    val languageCode = "${locale.language}-${locale.region}"
                    val languageParam = QueryParam("language", languageCode)

                    val newUrl = url.newBuilder().addQueryParams(languageParam).build()
                    builder.url(newUrl)
                }
                if (url.encodedPathSegments.contains("list")) {
                    builder.header("Content-Type", "application/json;charset=utf8")
                }
            }

            val response = chain.proceed(builder.build())

            handleResponseCode(response)

            return response
        }

        private fun shouldIncludeLanguageParam(urlSegments: List<String>): Boolean {
            val ignoredRoutes = listOf("list", "auth")
            for (route in ignoredRoutes) {
                if (urlSegments.contains(route)) {
                    return false
                }
            }
            return true
        }
    }

}