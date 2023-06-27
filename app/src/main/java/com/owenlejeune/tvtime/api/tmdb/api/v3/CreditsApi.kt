package com.owenlejeune.tvtime.api.tmdb.api.v3

import com.owenlejeune.tvtime.api.tmdb.api.v3.model.CreditResponse
import retrofit2.Response
import retrofit2.http.GET

interface CreditsApi {

    @GET("credit/{id}")
    suspend fun getCreditDetails(creditId: String): Response<CreditResponse>

}