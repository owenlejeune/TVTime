package com.owenlejeune.tvtime.api.nextmcu

import androidx.compose.runtime.mutableStateOf
import com.owenlejeune.tvtime.api.LoadingState
import com.owenlejeune.tvtime.api.nextmcu.model.NextMCU
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NextMCUService: KoinComponent {

    private val api: NextMCUApi by inject()

    val nextMcuProject = mutableStateOf<NextMCU?>(null)
    val nextMcuLoadingStateState = mutableStateOf(LoadingState.INACTIVE)

    suspend fun getNextMcuProject() {
        nextMcuLoadingStateState.value = LoadingState.LOADING
        val response = api.getNextMcuProject()
        if (response.isSuccessful) {
            response.body()?.let {
                nextMcuProject.value = it
                nextMcuLoadingStateState.value = LoadingState.COMPLETE
            } ?: run {
                nextMcuLoadingStateState.value = LoadingState.ERROR
            }
        } else {
            nextMcuLoadingStateState.value = LoadingState.ERROR
        }
    }

}