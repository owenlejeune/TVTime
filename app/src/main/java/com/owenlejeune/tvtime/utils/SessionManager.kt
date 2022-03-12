package com.owenlejeune.tvtime.utils

import com.owenlejeune.tvtime.api.tmdb.AccountService
import com.owenlejeune.tvtime.api.tmdb.AuthenticationService
import com.owenlejeune.tvtime.api.tmdb.GuestSessionService
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
                val deleteResponse = authenticationService.deleteSession(SessionBody(session.sessionId))
                withContext(Dispatchers.Main) {
                    if (deleteResponse.isSuccessful) {
                        _currentSession = null
                        preferences.guestSessionId = ""
                        preferences.authorizedSessionId = ""
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
            val session = AuthorizedSession()
            session.initialize()
            _currentSession = session
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

    suspend fun signInWithLogin(username: String, password: String): Boolean {
        val service = AuthenticationService()
        val createTokenResponse = service.createRequestToken()
        if (createTokenResponse.isSuccessful) {
            createTokenResponse.body()?.let { ctr ->
                val body = TokenValidationBody(username, password, ctr.requestToken)
                val loginResponse = service.validateTokenWithLogin(body)
                if (loginResponse.isSuccessful) {
                    loginResponse.body()?.let { lr ->
                        if (lr.success) {
                            val sessionBody = TokenSessionBody(lr.requestToken)
                            val sessionResponse = service.createSession(sessionBody)
                            if (sessionResponse.isSuccessful) {
                                sessionResponse.body()?.let { sr ->
                                    if (sr.isSuccess) {
                                        preferences.authorizedSessionId = sr.sessionId
                                        preferences.guestSessionId = ""
                                        _currentSession = AuthorizedSession()
                                        _currentSession?.initialize()
                                        return true
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false
    }

    abstract class Session(val sessionId: String, val isAuthorized: Boolean) {
        protected open var _ratedMovies: List<RatedMovie> = emptyList()
        val ratedMovies: List<RatedMovie>
            get() = _ratedMovies

        protected open var _ratedTvShows: List<RatedTv> = emptyList()
        val ratedTvShows: List<RatedTv>
            get() = _ratedTvShows

        protected open var _ratedTvEpisodes: List<RatedEpisode> = emptyList()
        val ratedTvEpisodes: List<RatedEpisode>
            get() = _ratedTvEpisodes

        protected open var _accountDetails: AccountDetails? = null
        val accountDetails: AccountDetails?
            get() = _accountDetails

        protected open var _accountLists: List<AccountList> = emptyList()
        val accountLists: List<AccountList>
            get() = _accountLists

        protected open var _favoriteMovies: List<FavoriteMovie> = emptyList()
        val favoriteMovies: List<FavoriteMovie>
            get() = _favoriteMovies

        protected open var _favoriteTvShows: List<FavoriteTvSeries> = emptyList()
        val favoriteTvShows: List<FavoriteTvSeries>
            get() = _favoriteTvShows

        protected open var _movieWatchlist: List<WatchlistMovie> = emptyList()
        val movieWatchlist: List<WatchlistMovie>
            get() = _movieWatchlist

        protected open var _tvWatchlist: List<WatchlistTvSeries> = emptyList()
        val tvWatchlist: List<WatchlistTvSeries>
            get() = _tvWatchlist

        fun hasRatedMovie(id: Int): Boolean {
            return ratedMovies.map { it.id }.contains(id)
        }

        fun hasRatedTvShow(id: Int): Boolean {
            return ratedTvShows.map { it.id }.contains(id)
        }

        fun hasRatedTvEpisode(id: Int): Boolean {
            return ratedTvEpisodes.map { it.id }.contains(id)
        }

        fun hasFavoritedMovie(id: Int): Boolean {
            return favoriteMovies.map { it.id }.contains(id)
        }

        fun hasFavoritedTvShow(id: Int): Boolean {
            return favoriteTvShows.map { it.id }.contains(id)
        }

        fun hasWatchlistedMovie(id: Int): Boolean {
            return movieWatchlist.map { it.id }.contains(id)
        }

        fun hasWatchlistedTvShow(id: Int): Boolean {
            return tvWatchlist.map { it.id }.contains(id)
        }

        abstract suspend fun initialize()

        abstract suspend fun refresh()
    }

    private class AuthorizedSession: Session(preferences.authorizedSessionId, true) {
        private val service by lazy { AccountService() }

        override suspend fun initialize() {
            refresh()
        }

        override suspend fun refresh() {
            service.getAccountDetails().apply {
                if (isSuccessful) {
                    withContext(Dispatchers.Main) {
                        _accountDetails = body() ?: _accountDetails
                        accountDetails?.let {
                            CoroutineScope(Dispatchers.IO).launch {
                                refreshWithAccountId(it.id)
                            }
                        }
                    }
                }
            }
        }

        private suspend fun refreshWithAccountId(accountId: Int) {
            service.getLists(accountId).apply {
                if (isSuccessful) {
                    withContext(Dispatchers.Main) {
                        _accountLists = body()?.results ?: _accountLists
                    }
                }
            }
            service.getFavoriteMovies(accountId).apply {
                if (isSuccessful) {
                    withContext(Dispatchers.Main) {
                        _favoriteMovies = body()?.results ?: _favoriteMovies
                    }
                }
            }
            service.getFavoriteTvShows(accountId).apply {
                if (isSuccessful) {
                    withContext(Dispatchers.Main) {
                        _favoriteTvShows = body()?.results ?: _favoriteTvShows
                    }
                }
            }
            service.getRatedMovies(accountId).apply {
                if (isSuccessful) {
                    withContext(Dispatchers.Main) {
                        _ratedMovies = body()?.results ?: _ratedMovies
                    }
                }
            }
            service.getRatedTvShows(accountId).apply {
                if (isSuccessful) {
                    withContext(Dispatchers.Main) {
                        _ratedTvShows = body()?.results ?: _ratedTvShows
                    }
                }
            }
            service.getRatedTvEpisodes(accountId).apply {
                if (isSuccessful) {
                    withContext(Dispatchers.Main) {
                        _ratedTvEpisodes = body()?.results ?: _ratedTvEpisodes
                    }
                }
            }
            service.getMovieWatchlist(accountId).apply {
                if (isSuccessful) {
                    withContext(Dispatchers.Main) {
                        _movieWatchlist = body()?.results ?: _movieWatchlist
                    }
                }
            }
            service.getTvWatchlist(accountId).apply {
                if (isSuccessful) {
                    withContext(Dispatchers.Main) {
                        _tvWatchlist = body()?.results ?: _tvWatchlist
                    }
                }
            }
        }
    }

    private class GuestSession: Session(preferences.guestSessionId, false) {
        private val service by lazy { GuestSessionService() }

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