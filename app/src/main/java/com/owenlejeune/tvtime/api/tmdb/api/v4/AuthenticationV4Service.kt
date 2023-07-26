package com.owenlejeune.tvtime.api.tmdb.api.v4

import com.owenlejeune.tvtime.api.tmdb.TmdbClient
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.StatusResponse
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.*
import retrofit2.Response

class AuthenticationV4Service {

    private val service by lazy { TmdbClient().createV4AuthenticationService() }

    suspend fun createRequestToken(redirect: String): Response<AuthResponse> {
        return service.createRequestToken(redirect)
    }

    suspend fun createAccessToken(requestToken: String): Response<AccessResponse> {
        return service.createAccessToken(requestToken)
    }

    suspend fun deleteAccessToken(body: AuthDeleteBody): Response<StatusResponse> {
        return service.deleteAccessToken(body)
    }

}