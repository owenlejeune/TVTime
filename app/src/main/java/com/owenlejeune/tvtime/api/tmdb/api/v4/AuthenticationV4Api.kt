package com.owenlejeune.tvtime.api.tmdb.api.v4

import com.owenlejeune.tvtime.api.tmdb.api.v3.model.StatusResponse
import com.owenlejeune.tvtime.api.tmdb.api.v4.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.HTTP
import retrofit2.http.POST

interface AuthenticationV4Api {

    @POST("auth/request_token")
    suspend fun createRequestToken(@Body body: AuthRequestBody): Response<AuthResponse>

    @POST("auth/access_token")
    suspend fun createAccessToken(@Body body: AuthAccessBody): Response<AccessResponse>

//    @DELETE("auth/access_token")
    @HTTP(method = "DELETE", path = "auth/access_token", hasBody = true)
    suspend fun deleteAccessToken(@Body body: AuthDeleteBody): Response<StatusResponse>

}