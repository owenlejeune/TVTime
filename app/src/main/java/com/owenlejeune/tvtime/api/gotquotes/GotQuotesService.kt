package com.owenlejeune.tvtime.api.gotquotes

import androidx.compose.runtime.mutableStateListOf
import com.owenlejeune.tvtime.api.gotquotes.model.GotQuote
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GotQuotesService: KoinComponent {

    private val api: GotQuotesApi by inject()

    val quotes = mutableStateListOf<GotQuote>()

    suspend fun getRandomQuotes() {
        quotes.clear()
        val response = api.getRandomQuotes()
        if (response.isSuccessful) {
            response.body()?.let {
                quotes.addAll(it)
            }
        }
    }

}