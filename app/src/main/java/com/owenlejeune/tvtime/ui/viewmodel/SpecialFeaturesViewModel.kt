package com.owenlejeune.tvtime.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.owenlejeune.tvtime.api.gotquotes.GotQuotesService
import com.owenlejeune.tvtime.api.nextmcu.NextMCUService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SpecialFeaturesViewModel: ViewModel(), KoinComponent {

    private val mcuService: NextMCUService by inject()
    private val gotQuotesService: GotQuotesService by inject()

    val nextMcuProject = mcuService.nextMcuProject
    val gotQuotes = gotQuotesService.quotes

    suspend fun getNextMcuProject(force: Boolean = false) {
        if (nextMcuProject.value == null || force) {
            mcuService.getNextMcuProject()
        }
    }

    suspend fun getGotQuotes() {
        gotQuotesService.getRandomQuotes()
    }

}