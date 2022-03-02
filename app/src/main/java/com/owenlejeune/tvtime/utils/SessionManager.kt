package com.owenlejeune.tvtime.utils

import com.owenlejeune.tvtime.api.tmdb.GuestSessionApi
import com.owenlejeune.tvtime.api.tmdb.TmdbClient
import com.owenlejeune.tvtime.api.tmdb.model.RatedEpisode
import com.owenlejeune.tvtime.api.tmdb.model.RatedMedia
import com.owenlejeune.tvtime.api.tmdb.model.RatedMovie
import com.owenlejeune.tvtime.api.tmdb.model.RatedTv
import com.owenlejeune.tvtime.preferences.AppPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object SessionManager: KoinComponent {

    private val preferences: AppPreferences by inject()

    private var _currentSession: Session? = null
    val currentSession: Session
        get() = _currentSession!!

    private val authenticationService by lazy { TmdbClient().createAuthenticationService() }

    suspend fun initialize() {
        _currentSession = if (preferences.guestSessionId.isNotEmpty()) {
            val session = GuestSession()
            session.initialize()
            session
        } else {
            requestNewGuestSession()
        }
    }

    private suspend fun requestNewGuestSession(): Session? {
        val response = authenticationService.getNewGuestSession()
        if (response.isSuccessful) {
            preferences.guestSessionId = response.body()?.guestSessionId ?: ""
            _currentSession = GuestSession()
        }
        return _currentSession
    }

    abstract class Session(val sessionId: String, val isGuest: Boolean) {
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

    private class GuestSession: Session(preferences.guestSessionId, true) {
        override var _ratedMovies: List<RatedMovie> = emptyList()
        override var _ratedTvShows: List<RatedTv> = emptyList()
        override var _ratedTvEpisodes: List<RatedEpisode> = emptyList()

        private lateinit var service: GuestSessionApi

        override suspend fun initialize() {
            service = TmdbClient().createGuestSessionService()
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