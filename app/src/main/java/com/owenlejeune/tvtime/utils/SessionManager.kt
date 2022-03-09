package com.owenlejeune.tvtime.utils

import com.owenlejeune.tvtime.api.tmdb.TmdbClient
import com.owenlejeune.tvtime.api.tmdb.model.*
import com.owenlejeune.tvtime.preferences.AppPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object SessionManager: KoinComponent {

    private val preferences: AppPreferences by inject()

    private var _currentSession: Session? = null
    val currentSession: Session?
        get() = _currentSession

    private val authenticationService by lazy { TmdbClient().createAuthenticationService() }

    fun clearSession(onResponse: (isSuccessful: Boolean) -> Unit) {
        currentSession?.let { session ->
            CoroutineScope(Dispatchers.IO).launch {
                val deleteResponse = authenticationService.deleteSession(
                    SessionBody(
                        session.sessionId
                    )
                )
                withContext(Dispatchers.Main) {
                    if (deleteResponse.isSuccessful) {
                        _currentSession = null
                        preferences.guestSessionId = ""
                    }
                    onResponse(deleteResponse.isSuccessful)
                }
            }
        }
    }

    suspend fun initialize() {
        if (preferences.guestSessionId.isNotEmpty()) {
            val session = GuestSession()
            session.initialize()
            _currentSession = session
        } else if (preferences.authorizedSessionId.isNotEmpty()) {

        }
    }

    suspend fun requestNewGuestSession(): Session? {
        val response = authenticationService.getNewGuestSession()
        if (response.isSuccessful) {
            preferences.guestSessionId = response.body()?.guestSessionId ?: ""
            _currentSession = GuestSession()
        }
        return _currentSession
    }

    suspend fun signInWithLogin(email: String, password: String): Boolean {
        val service = TmdbClient().createAuthenticationService()
        val createTokenResponse = service.createRequestToken()
        if (createTokenResponse.isSuccessful) {
            createTokenResponse.body()?.let { ctr ->
                val body = TokenValidationBody(email, password, ctr.requestToken)
                val loginResponse = service.validateTokenWithLogin(body)
                if (loginResponse.isSuccessful) {
                    loginResponse.body()?.let { lr ->
                        if (lr.success) {
                            preferences.authorizedSessionId = lr.requestToken
                            _currentSession = AuthorizedSession()
                            _currentSession?.initialize()
                            return true
                        }
                    }
                }
            }
        }
        return false
    }

    abstract class Session(val sessionId: String, val isAuthorized: Boolean) {
        protected abstract var _ratedMovies: List<RatedMovie>
        val ratedMovies: List<RatedMovie>
            get() = _ratedMovies

        protected abstract var _ratedTvShows: List<RatedTv>
        val ratedTvShows: List<RatedTv>
            get() = _ratedTvShows

        protected abstract var _ratedTvEpisodes: List<RatedEpisode>
        val ratedTvEpisodes: List<RatedEpisode>
            get() = _ratedTvEpisodes

        fun hasRatedMovie(id: Int): Boolean {
            return ratedMovies.map { it.id }.contains(id)
        }

        fun hasRatedTvShow(id: Int): Boolean {
            return ratedTvShows.map { it.id }.contains(id)
        }

        fun hasRatedTvEpisode(id: Int): Boolean {
            return ratedTvEpisodes.map { it.id }.contains(id)
        }

        abstract suspend fun initialize()

        abstract suspend fun refresh()
    }

    private class AuthorizedSession: Session(preferences.authorizedSessionId, true) {
        override var _ratedMovies: List<RatedMovie> = emptyList()
        override var _ratedTvShows: List<RatedTv> = emptyList()
        override var _ratedTvEpisodes: List<RatedEpisode> = emptyList()

        override suspend fun initialize() {
            refresh()
        }

        override suspend fun refresh() {

        }
    }

    private class GuestSession: Session(preferences.guestSessionId, false) {
        override var _ratedMovies: List<RatedMovie> = emptyList()
        override var _ratedTvShows: List<RatedTv> = emptyList()
        override var _ratedTvEpisodes: List<RatedEpisode> = emptyList()

        private val service by lazy { TmdbClient().createGuestSessionService() }

        override suspend fun initialize() {
            refresh()
        }

        override suspend fun refresh() {
            service.getRatedMovies(sessionId).apply {
                if (isSuccessful) {
                    withContext(Dispatchers.Main) {
                        _ratedMovies = body()?.results ?: _ratedMovies
                    }
                }
            }
            service.getRatedTvShows(sessionId).apply {
                if (isSuccessful) {
                    withContext(Dispatchers.Main) {
                        _ratedTvShows = body()?.results ?: _ratedTvShows
                    }
                }
            }
            service.getRatedTvEpisodes(sessionId).apply {
                if (isSuccessful) {
                    withContext(Dispatchers.Main) {
                        _ratedTvEpisodes = body()?.results ?: _ratedTvEpisodes
                    }
                }
            }
        }
    }

}