package com.owenlejeune.tvtime.api.tmdb.api.v3

import com.owenlejeune.tvtime.api.tmdb.api.v3.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST

interface AuthenticationApi {

    @FormUrlEncoded
    @POST("authentication/session/convert/4")
    suspend fun createSessionFromV4Token(@Field("access_token") accessToken: String): Response<CreateSessionResponse>
}