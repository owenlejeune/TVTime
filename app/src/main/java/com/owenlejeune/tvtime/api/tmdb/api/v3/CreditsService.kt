package com.owenlejeune.tvtime.api.tmdb.api.v3

import androidx.compose.runtime.mutableStateMapOf
import com.owenlejeune.tvtime.api.tmdb.api.v3.model.CreditResponse
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.Collections

class CreditsService: KoinComponent {

    private val creditApi: CreditsApi by inject()

    val credits = Collections.synchronizedMap(mutableStateMapOf<String, CreditResponse>())

    suspend fun getCredits(creditId: String) {
        val response = creditApi.getCreditDetails(creditId)
        if (response.isSuccessful) {
            response.body()?.let {
                credits[creditId] = it
            }
        }
    }

}