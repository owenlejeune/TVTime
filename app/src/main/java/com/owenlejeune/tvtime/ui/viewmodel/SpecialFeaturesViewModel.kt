package com.owenlejeune.tvtime.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.owenlejeune.tvtime.api.nextmcu.NextMCUService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SpecialFeaturesViewModel: ViewModel(), KoinComponent {

    private val mcuService: NextMCUService by inject()

    val nextMcuProject = mcuService.nextMcuProject

    suspend fun getNextMcuProject() {
        if (nextMcuProject.value == null) {
            mcuService.getNextMcuProject()
        }
    }

}