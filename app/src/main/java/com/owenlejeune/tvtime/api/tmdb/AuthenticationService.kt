package com.owenlejeune.tvtime.api.tmdb

import com.owenlejeune.tvtime.api.tmdb.model.SessionBody
import com.owenlejeune.tvtime.api.tmdb.model.DeleteSessionResponse
import com.owenlejeune.tvtime.api.tmdb.model.GuestSessionResponse
import retrofit2.Response

class AuthenticationService {

    private val service by lazy { TmdbClient().createAuthenticationService() }

    suspend fun getNewGuestSession(): Response<GuestSessionResponse> {
        return service.getNewGuestSession()
    }

    suspend fun deleteSession(body: SessionBody): Response<DeleteSessionResponse> {
        return service.deleteSession(body)
    }

}