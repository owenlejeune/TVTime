package com.owenlejeune.tvtime.api.gotquotes

import com.owenlejeune.tvtime.api.gotquotes.model.GotQuote
import retrofit2.Response
import retrofit2.http.GET

interface GotQuotesApi {

    @GET("random/5")
    suspend fun getRandomQuotes(): Response<List<GotQuote>>

}