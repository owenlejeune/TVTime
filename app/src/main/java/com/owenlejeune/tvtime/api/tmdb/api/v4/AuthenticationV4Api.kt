package com.owenlejeune.tvtime.api.tmdb.api.v4

import com.owenlejeune.tvtime.api.tmdb.api.v3.model.StatusResponse
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.*
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.POST

interface AuthenticationV4Api {

    @POST("auth/request_token")
    suspend fun createRequestToken(body: AuthRequestBody): Response<AuthResponse>

    @POST("auth/access_token")
    suspend fun createAccessToken(body: AuthAccessBody): Response<AccessResponse>

    @DELETE("auth/access_token")
    suspend fun deleteAccessToken(body: AuthDeleteBody): Response<StatusResponse>

}