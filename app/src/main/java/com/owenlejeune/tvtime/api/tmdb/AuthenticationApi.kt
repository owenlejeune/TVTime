package com.owenlejeune.tvtime.api.tmdb

import com.owenlejeune.tvtime.api.tmdb.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST

interface AuthenticationApi {

    @GET("authentication/guest_session/new")
    suspend fun getNewGuestSession(): Response<GuestSessionResponse>

    @HTTP(method = "DELETE", path = "authentication/session", hasBody = true)
    suspend fun deleteSession(@Body body: SessionBody): Response<DeleteSessionResponse>

    @GET("authentication/token/new")
    suspend fun createRequestToken(): Response<CreateTokenResponse>

    @POST("authentication/session/new")
    suspend fun createSession(@Body body: SessionBody): Response<CreateSessionResponse>

    @POST("authentication/token/validate_with_login")
    suspend fun validateTokenWithLogin(@Body body: TokenValidationBody): Response<CreateTokenResponse>
}