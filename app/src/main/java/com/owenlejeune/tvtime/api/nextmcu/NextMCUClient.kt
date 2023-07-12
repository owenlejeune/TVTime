package com.owenlejeune.tvtime.api.nextmcu

import com.owenlejeune.tvtime.api.Client
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class NextMCUClient: KoinComponent {

    companion object {
        private const val BASE_URL = "https://dev.whenisthenextmcufilm.com/"
    }

    private val client: Client by inject { parametersOf(BASE_URL) }

    fun createNextMcuService(): NextMCUApi {
        return client.create(NextMCUApi::class.java)
    }

}