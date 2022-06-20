package com.owenlejeune.tvtime.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.api.tmdb.api.v3.AccountService
import com.owenlejeune.tvtime.api.tmdb.api.v3.AuthenticationService
import com.owenlejeune.tvtime.api.tmdb.api.v3.GuestSessionService
import com.owenlejeune.tvtime.api.tmdb.TmdbClient
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.*
import com.owenlejeune.tvtime.api.tmdb.api.v4.AuthenticationV4Service
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.AuthAccessBody
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.AuthDeleteBody
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.AuthRequestBody
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

    var isV4SignInInProgress: Boolean = false

    private val authenticationService by lazy { TmdbClient().createAuthenticationService() }
    private val authenticationV4Service by lazy { TmdbClient().createV4AuthenticationService() }

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

    fun clearSessionV4(onResponse: (isSuccessful: Boolean) -> Unit) {
        currentSession?.let { session ->
            CoroutineScope(Dispatchers.IO).launch {
                val deleteResponse = authenticationV4Service.deleteAccessToken(AuthDeleteBody(session.sessionId))
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

    suspend fun signInWithV4Part1(context: Context) {
        isV4SignInInProgress = true

        val service = AuthenticationV4Service()
        val requestTokenResponse = service.createRequestToken(AuthRequestBody(redirect = ""))
        if (requestTokenResponse.isSuccessful) {
            requestTokenResponse.body()?.let { ctr ->
                _currentSession = InProgressSession(ctr.requestToken)
                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(
                        context.getString(R.string.tmdb_auth_url, ctr.requestToken)
                    )
                )
                context.startActivity(browserIntent)
            }
        }
    }

    suspend fun signInWithV4Part2(): Boolean {
        if (isV4SignInInProgress && _currentSession is InProgressSession) {
            val requestToken = _currentSession!!.sessionId
            val authResponse = authenticationV4Service.createAccessToken(AuthAccessBody(requestToken))
            if (authResponse.isSuccessful) {
                authResponse.body()?.let { ar ->
                    if (ar.success) {
                        val sessionResponse = authenticationService.createSessionFromV4Token(V4TokenBody(ar.accessToken))
                        if (sessionResponse.isSuccessful) {
                            sessionResponse.body()?.let { sr ->
                                preferences.authorizedSessionId = sr.sessionId
                                preferences.guestSessionId = ""
                                _currentSession = AuthorizedSession(accessToken = ar.accessToken)
                                _currentSession?.initialize()
                                isV4SignInInProgress = false
                                return true
                            }
                        }
//                        preferences.authorizedSessionId = ar.accessToken
//                        preferences.guestSessionId = ""
//                        _currentSession = AuthorizedSession()
//                        _currentSession?.initialize()
//                        isV4SignInInProgress = false
//                        return true
                    }
                }
            }
        }
        return false
    }

    abstract class Session(val sessionId: String, val isAuthorized: Boolean, val accessToken: String = "") {
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

        fun getRatingForId(id: Int): Float {
            return ratedMovies.firstOrNull { it.id == id }?.rating
                ?: ratedTvShows.firstOrNull { it.id == id }?.rating
                ?: ratedTvEpisodes.firstOrNull { it.id == id }?.rating
                ?: 0f
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

        abstract suspend fun refresh(changed: Array<Changed> = Changed.All)

        enum class Changed {
            AccountDetails,
            Lists,
            RatedMovies,
            RatedTv,
            RatedEpisodes,
            FavoriteMovies,
            FavoriteTv,
            WatchlistMovies,
            WatchlistTv;

            companion object {
                val All get() = values()
                val Rated get() = arrayOf(RatedMovies, RatedTv, RatedEpisodes)
                val Favorites get() = arrayOf(FavoriteMovies, FavoriteTv)
                val Watchlist get() = arrayOf(WatchlistMovies, WatchlistTv)
            }
        }
    }

    private class InProgressSession(requestToken: String): Session(requestToken, false) {
        override suspend fun initialize() {
            // do nothing
        }

        override suspend fun refresh(changed: Array<Changed>) {
            // do nothing
        }

    }

    private class AuthorizedSession(accessToken: String = ""): Session(preferences.authorizedSessionId, true, accessToken) {
        private val service by lazy { AccountService() }

        override suspend fun initialize() {
            refresh()
        }

        override suspend fun refresh(changed: Array<Changed>) {
            if (changed.contains(Changed.AccountDetails)) {
                service.getAccountDetails().apply {
                    if (isSuccessful) {
                        withContext(Dispatchers.Main) {
                            _accountDetails = body() ?: _accountDetails
                            accountDetails?.let {
                                CoroutineScope(Dispatchers.IO).launch {
                                    refreshWithAccountId(it.id, changed)
                                }
                            }
                        }
                    }
                }
            } else if (accountDetails != null) {
                refreshWithAccountId(accountDetails!!.id, changed)
            }
        }

        private suspend fun refreshWithAccountId(accountId: Int, changed: Array<Changed> = Changed.All) {
            if (changed.contains(Changed.Lists)) {
                service.getLists(accountId).apply {
                    if (isSuccessful) {
                        withContext(Dispatchers.Main) {
                            _accountLists = body()?.results ?: _accountLists
                        }
                    }
                }
            }
            if (changed.contains(Changed.FavoriteMovies)) {
                service.getFavoriteMovies(accountId).apply {
                    if (isSuccessful) {
                        withContext(Dispatchers.Main) {
                            _favoriteMovies = body()?.results ?: _favoriteMovies
                        }
                    }
                }
            }
            if (changed.contains(Changed.FavoriteTv)) {
                service.getFavoriteTvShows(accountId).apply {
                    if (isSuccessful) {
                        withContext(Dispatchers.Main) {
                            _favoriteTvShows = body()?.results ?: _favoriteTvShows
                        }
                    }
                }
            }
            if (changed.contains(Changed.RatedMovies)) {
                service.getRatedMovies(accountId).apply {
                    if (isSuccessful) {
                        withContext(Dispatchers.Main) {
                            _ratedMovies = body()?.results ?: _ratedMovies
                        }
                    }
                }
            }
            if (changed.contains(Changed.RatedTv)) {
                service.getRatedTvShows(accountId).apply {
                    if (isSuccessful) {
                        withContext(Dispatchers.Main) {
                            _ratedTvShows = body()?.results ?: _ratedTvShows
                        }
                    }
                }
            }
            if (changed.contains(Changed.RatedEpisodes)) {
                service.getRatedTvEpisodes(accountId).apply {
                    if (isSuccessful) {
                        withContext(Dispatchers.Main) {
                            _ratedTvEpisodes = body()?.results ?: _ratedTvEpisodes
                        }
                    }
                }
            }
            if (changed.contains(Changed.WatchlistMovies)) {
                service.getMovieWatchlist(accountId).apply {
                    if (isSuccessful) {
                        withContext(Dispatchers.Main) {
                            _movieWatchlist = body()?.results ?: _movieWatchlist
                        }
                    }
                }
            }
            if (changed.contains(Changed.WatchlistTv)) {
                service.getTvWatchlist(accountId).apply {
                    if (isSuccessful) {
                        withContext(Dispatchers.Main) {
                            _tvWatchlist = body()?.results ?: _tvWatchlist
                        }
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

        override suspend fun refresh(changed: Array<Changed>) {
            if (changed.contains(Changed.RatedMovies)) {
                service.getRatedMovies(sessionId).apply {
                    if (isSuccessful) {
                        withContext(Dispatchers.Main) {
                            _ratedMovies = body()?.results ?: _ratedMovies
                        }
                    }
                }
            }
            if (changed.contains(Changed.RatedTv)) {
                service.getRatedTvShows(sessionId).apply {
                    if (isSuccessful) {
                        withContext(Dispatchers.Main) {
                            _ratedTvShows = body()?.results ?: _ratedTvShows
                        }
                    }
                }
            }
            if (changed.contains(Changed.RatedEpisodes)) {
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

}