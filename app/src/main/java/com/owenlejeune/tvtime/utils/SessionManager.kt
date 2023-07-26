package com.owenlejeune.tvtime.utils

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import com.google.gson.annotations.SerializedName
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.api.tmdb.api.v3.AccountService
import com.owenlejeune.tvtime.api.tmdb.api.v3.AuthenticationService
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.*
import com.owenlejeune.tvtime.api.tmdb.api.v4.AuthenticationV4Service
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.AuthDeleteBody
import com.owenlejeune.tvtime.preferences.AppPreferences
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
        val requestTokenResponse = service.createRequestToken(redirect = "app://tvtime.auth.return")
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
            val authResponse = authenticationV4Service.createAccessToken(requestToken)
            if (authResponse.isSuccessful) {
                authResponse.body()?.let { ar ->
                    if (ar.success) {
                        val sessionResponse = authenticationService.createSessionFromV4Token(ar.accessToken)
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

        val accountDetails = mutableStateOf<AccountDetails?>(null)

        abstract suspend fun initialize()

    }

    private class InProgressSession(requestToken: String): Session(requestToken, false) {
        override suspend fun initialize() {
            // do nothing
        }

    }

    private class AuthorizedSession(
        sessionId: String = "",
        accessToken: String = "",
        accountId: String = ""
    ): Session(sessionId, true, accessToken, accountId) {
        private val service: AccountService by inject()

        override suspend fun initialize() {
            val response = service.getAccountDetails()
            if (response.isSuccessful) {
                accountDetails.value = response.body()
            }
        }
    }
}