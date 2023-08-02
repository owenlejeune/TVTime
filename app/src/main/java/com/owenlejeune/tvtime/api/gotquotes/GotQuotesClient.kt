package com.owenlejeune.tvtime.api.gotquotes

import com.owenlejeune.tvtime.api.common.Client
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class GotQuotesClient: KoinComponent {

    companion object {
        private const val BASE_URL = "https://api.gameofthronesquotes.xyz/v1/"
    }

    private val client: Client by inject { parametersOf(BASE_URL) }

    fun createQuotesApi(): GotQuotesApi {
        return client.create(GotQuotesApi::class.java)
    }

}