package com.owenlejeune.tvtime.api.tmdb

import com.owenlejeune.tvtime.api.tmdb.model.DeleteSessionBody
import com.owenlejeune.tvtime.api.tmdb.model.DeleteSessionResponse
import com.owenlejeune.tvtime.api.tmdb.model.GuestSessionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET

interface AuthenticationApi {

    @GET("authentication/guest_session/new")
    suspend fun getNewGuestSession(): Response<GuestSessionResponse>

    @DELETE("authentication/session")
    suspend fun deleteSession(@Body body: DeleteSessionBody): Response<DeleteSessionResponse>

}