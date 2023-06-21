package com.owenlejeune.tvtime.utils

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.google.gson.annotations.SerializedName
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.api.tmdb.TmdbClient
import com.owenlejeune.tvtime.api.tmdb.api.v3.AccountService
import com.owenlejeune.tvtime.api.tmdb.api.v3.AuthenticationService
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.*
import com.owenlejeune.tvtime.api.tmdb.api.v4.AccountV4Service
import com.owenlejeune.tvtime.api.tmdb.api.v4.AuthenticationV4Service
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.AuthAccessBody
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.AuthDeleteBody
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.AuthRequestBody
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.AccountList
import com.owenlejeune.tvtime.preferences.AppPreferences
import com.owenlejeune.tvtime.utils.types.MediaViewType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.java.KoinJavaComponent.get
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object SessionManager: KoinComponent {

    private val preferences: AppPreferences by inject()
    private val authenticationService: AuthenticationService by inject()
    private val authenticationV4Service: AuthenticationV4Service by inject()

    val currentSession = mutableStateOf<Session?>(null)

    class AuthorizedSessionValues(
        @SerializedName("session_id") val sessionId: String,
        @SerializedName("access_token") val accessToken: String,
        @SerializedName("account_id") val accountId: String
    )

    fun clearSession() {
        currentSession.value?.let { session ->
            CoroutineScope(Dispatchers.IO).launch {
                val deleteResponse = authenticationV4Service.deleteAccessToken(AuthDeleteBody(session.accessToken))
                withContext(Dispatchers.Main) {
                    if (deleteResponse.isSuccessful) {
                        currentSession.value = null
                        preferences.authorizedSessionValues = null
                    }
                }
            }
        }
    }

    fun cancelSignIn() {
        if (currentSession.value is InProgressSession) {
            currentSession.value = null
        }
    }

    suspend fun initialize() {
        preferences.authorizedSessionValues?.let { values ->
            val session = AuthorizedSession(
                sessionId = values.sessionId,
                accessToken = values.accessToken,
                accountId = values.accountId
            )
            currentSession.value = session
            session.initialize()
        }
    }

    suspend fun signInPart1(
        context: Context,
        onRedirect: (url: String) -> Unit
    ) {
        val service = AuthenticationV4Service()
        val requestTokenResponse = service.createRequestToken(AuthRequestBody(redirect = "app://tvtime.auth.return"))
        if (requestTokenResponse.isSuccessful) {
            requestTokenResponse.body()?.let { ctr ->
                val url = context.getString(R.string.tmdb_auth_url, ctr.requestToken)
                val encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
                withContext(Dispatchers.Main) {
                    onRedirect(encodedUrl)
                }
                currentSession.value = InProgressSession(ctr.requestToken)
            }
        }
    }

    suspend fun signInPart2(
        context: Context = get(Context::class.java)
    ) {
        if (currentSession.value is InProgressSession) {
            val requestToken = currentSession.value!!.sessionId
            val authResponse = authenticationV4Service.createAccessToken(AuthAccessBody(requestToken))
            if (authResponse.isSuccessful) {
                authResponse.body()?.let { ar ->
                    if (ar.success) {
                        val sessionResponse = authenticationService.createSessionFromV4Token(V4TokenBody(ar.accessToken))
                        if (sessionResponse.isSuccessful) {
                            sessionResponse.body()?.let { sr ->
                                preferences.authorizedSessionValues = AuthorizedSessionValues(
                                    sessionId = sr.sessionId,
                                    accountId = ar.accountId,
                                    accessToken = ar.accessToken
                                )
                                val session = AuthorizedSession(
                                    sessionId = sr.sessionId,
                                    accessToken = ar.accessToken,
                                    accountId = ar.accountId
                                )
                                currentSession.value = session
                                session.initialize()
                            }
                        }
                    } else {
                        currentSession.value = null
                        Toast.makeText(
                            context,
                            "Error signing in",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    abstract class Session(val sessionId: String, val isAuthorized: Boolean, val accessToken: String = "", val accountId: String = "") {

        val ratedMovies = mutableStateListOf<RatedMovie>()
        val ratedTvShows = mutableStateListOf<RatedTv>()
        val ratedTvEpisodes = mutableStateListOf<RatedEpisode>()
        val accountDetails = mutableStateOf<AccountDetails?>(null)
        val accountLists = mutableStateListOf<AccountList>()
        val favoriteMovies = mutableStateListOf<FavoriteMovie>()
        val favoriteTvShows = mutableStateListOf<FavoriteTvSeries>()
        val movieWatchlist = mutableStateListOf<WatchlistMovie>()
        val tvWatchlist = mutableStateListOf<WatchlistTvSeries>()

        fun hasRatedMovie(id: Int): Boolean {
            return ratedMovies.map { it.id }.contains(id)
        }

        fun hasRatedTvShow(id: Int): Boolean {
            return ratedTvShows.map { it.id }.contains(id)
        }

        fun hasRatedTvEpisode(id: Int): Boolean {
            return ratedTvEpisodes.map { it.id }.contains(id)
        }

        fun getRatingForId(id: Int, type: MediaViewType): Float? {
            return when(type) {
                MediaViewType.MOVIE -> ratedMovies.firstOrNull { it.id == id }?.rating
                MediaViewType.TV -> ratedTvShows.firstOrNull { it.id == id }?.rating
                MediaViewType.EPISODE -> ratedTvEpisodes.firstOrNull { it.id == id }?.rating
                else -> null
            }
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
                val List get() = arrayOf(Lists)
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

    private class AuthorizedSession(
        sessionId: String = "",
        accessToken: String = "",
        accountId: String = ""
    ): Session(sessionId, true, accessToken, accountId) {
        private val service: AccountService by inject()
        private val serviceV4: AccountV4Service by inject()

        override suspend fun initialize() {
            refresh()
        }

        override suspend fun refresh(changed: Array<Changed>) {
            if (changed.contains(Changed.AccountDetails)) {
                val response = service.getAccountDetails()
                if (response.isSuccessful) {
                    accountDetails.value = response.body()
                    accountDetails.value?.let {
                        refreshWithAccountId(it.id, changed)
                    }
                }
            } else if (accountDetails.value != null) {
                refreshWithAccountId(accountDetails.value!!.id, changed)
            }
        }

        private suspend fun refreshWithAccountId(accountId: Int, changed: Array<Changed> = Changed.All) {
            if (changed.contains(Changed.Lists)) {
                serviceV4.getLists(preferences.authorizedSessionValues?.accountId ?: "").apply {
                    if (isSuccessful) {
                        withContext(Dispatchers.Main) {
                            body()?.results?.let {
                                accountLists.clear()
                                accountLists.addAll(it)
                            }
                        }
                    }
                }
            }
            if (changed.contains(Changed.FavoriteMovies)) {
                service.getFavoriteMovies(accountId).apply {
                    if (isSuccessful) {
                        withContext(Dispatchers.Main) {
                            body()?.results?.let {
                                favoriteMovies.clear()
                                favoriteMovies.addAll(it)
                            }
                        }
                    }
                }
            }
            if (changed.contains(Changed.FavoriteTv)) {
                service.getFavoriteTvShows(accountId).apply {
                    if (isSuccessful) {
                        withContext(Dispatchers.Main) {
                            body()?.results?.let {
                                favoriteTvShows.clear()
                                favoriteTvShows.addAll(it)
                            }
                        }
                    }
                }
            }
            if (changed.contains(Changed.RatedMovies)) {
                service.getRatedMovies(accountId).apply {
                    if (isSuccessful) {
                        withContext(Dispatchers.Main) {
                            body()?.results?.let {
                                ratedMovies.clear()
                                ratedMovies.addAll(it)
                            }
                        }
                    }
                }
            }
            if (changed.contains(Changed.RatedTv)) {
                service.getRatedTvShows(accountId).apply {
                    if (isSuccessful) {
                        withContext(Dispatchers.Main) {
                            body()?.results?.let {
                                ratedTvShows.clear()
                                ratedTvShows.addAll(it)
                            }
                        }
                    }
                }
            }
            if (changed.contains(Changed.RatedEpisodes)) {
                service.getRatedTvEpisodes(accountId).apply {
                    if (isSuccessful) {
                        withContext(Dispatchers.Main) {
                            body()?.results?.let {
                                ratedTvEpisodes.clear()
                                ratedTvEpisodes.addAll(it)
                            }
                        }
                    }
                }
            }
            if (changed.contains(Changed.WatchlistMovies)) {
                service.getMovieWatchlist(accountId).apply {
                    if (isSuccessful) {
                        withContext(Dispatchers.Main) {
                            body()?.results?.let {
                                movieWatchlist.clear()
                                movieWatchlist.addAll(it)
                            }
                        }
                    }
                }
            }
            if (changed.contains(Changed.WatchlistTv)) {
                service.getTvWatchlist(accountId).apply {
                    if (isSuccessful) {
                        withContext(Dispatchers.Main) {
                            body()?.results?.let {
                                tvWatchlist.clear()
                                tvWatchlist.addAll(it)
                            }
                        }
                    }
                }
            }
        }
    }
}