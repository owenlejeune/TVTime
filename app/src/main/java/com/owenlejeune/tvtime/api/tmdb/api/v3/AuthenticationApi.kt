package com.owenlejeune.tvtime.api.tmdb.api.v3

import com.owenlejeune.tvtime.api.tmdb.api.v3.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST

interface AuthenticationApi {

    @POST("authentication/session/convert/4")
    suspend fun createSessionFromV4Token(@Body body: V4TokenBody): Response<CreateSessionResponse>
}