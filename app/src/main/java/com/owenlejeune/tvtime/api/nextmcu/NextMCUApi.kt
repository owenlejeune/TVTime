package com.owenlejeune.tvtime.api.nextmcu

import com.owenlejeune.tvtime.api.nextmcu.model.NextMCU
import retrofit2.Response
import retrofit2.http.GET

interface NextMCUApi {

    @GET("api")
    suspend fun getNextMcuProject(): Response<NextMCU>

}