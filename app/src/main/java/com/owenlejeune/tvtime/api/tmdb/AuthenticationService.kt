package com.owenlejeune.tvtime.api.tmdb

import com.owenlejeune.tvtime.api.tmdb.model.*
import retrofit2.Response

class AuthenticationService {

    private val service by lazy { TmdbClient().createAuthenticationService() }

    suspend fun getNewGuestSession(): Response<GuestSessionResponse> {
        return service.getNewGuestSession()
    }

    suspend fun deleteSession(body: SessionBody): Response<DeleteSessionResponse> {
        return service.deleteSession(body)
    }

    suspend fun createRequestToken(): Response<CreateTokenResponse> {
        return service.createRequestToken()
    }

    suspend fun createSession(body: TokenSessionBody): Response<CreateSessionResponse> {
        return service.createSession(body)
    }

    suspend fun validateTokenWithLogin(body: TokenValidationBody): Response<CreateTokenResponse> {
        return service.validateTokenWithLogin(body)
    }
}