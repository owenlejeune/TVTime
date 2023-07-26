package com.owenlejeune.tvtime.api.tmdb.api.v3

import com.owenlejeune.tvtime.api.tmdb.TmdbClient
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.*
import retrofit2.Response

class AuthenticationService {

    private val service by lazy { TmdbClient().createAuthenticationService() }

    suspend fun createSessionFromV4Token(accessToken: String): Response<CreateSessionResponse> {
        return service.createSessionFromV4Token(accessToken)
    }
}